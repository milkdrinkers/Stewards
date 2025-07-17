package io.github.milkdrinkers.stewards.listener;

import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.event.HoverEvent;
import com.palmergames.adventure.text.format.NamedTextColor;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.PreDeleteTownEvent;
import com.palmergames.bukkit.towny.event.economy.TownPreTransactionEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.economy.transaction.TransactionType;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.alathra.alathraports.core.carriagestations.CarriageStation;
import io.github.alathra.alathraports.core.ports.Port;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.steward.lookup.StewardLookup;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.stewards.utility.DeleteUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class TownyListener implements Listener {
    private final Stewards plugin;
    private final @NotNull StewardLookup lookup;

    public TownyListener(Stewards plugin) {
        this.plugin = plugin;
        this.lookup = plugin.getStewardLookup();
    }

    @EventHandler
    public void onTownyDeposit(TownPreTransactionEvent e) {
        if (e.getTransaction().getType() != TransactionType.DEPOSIT) return;

        if (e.getTransaction().getSendingPlayer() == null) return;

        final Town town = e.getTown();

        if ((e.getTransaction().getReceivingAccount().getHoldingBalance() + e.getTransaction().getAmount()) > TownMetaData.getBankLimit(town)) {
            e.setCancelMessage("You can't transfer that much money into your town bank. Your town bank limit is: " + TownMetaData.getBankLimit(town));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTownStatusScreen(TownStatusScreenEvent e) {
        Component hoverComponent;

        final StewardType type = StewardsAPI.getRegistry().getType(StewardTypeHandler.TREASURER_ID);

        if (TownMetaData.NPC.has(e.getTown(), type)) {
            hoverComponent = Component.text("Your Treasurer is level " + TownMetaData.NPC.getStewardOptional(e.getTown(), type).map(Steward::getLevel).orElse(0) + ". To increase this limit, upgrade your Treasurer.", NamedTextColor.GRAY);
        } else {
            hoverComponent = Component.text("You don't have a Treasurer. To increase this limit, hire a Treasurer.", NamedTextColor.GRAY);
        }

        Component bankLimit = Component.newline().append(Component.text("[", NamedTextColor.GRAY)).append(Component.text("Stewards", NamedTextColor.GREEN)).append(Component.text("] ", NamedTextColor.GRAY)).append(Component.text("Town bank limit: %s⊚.".formatted(TownMetaData.getBankLimit(e.getTown())), NamedTextColor.WHITE)).hoverEvent(HoverEvent.showText(hoverComponent));

        e.getStatusScreen().addComponentOf("Stewards", bankLimit);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTownRemoved(DeleteTownEvent e) {
        for (Steward steward : lookup.town().getTownStewards(e.getTownUUID())) {
            DeleteUtils.dismiss(steward, e.getTownUUID(), null, false);
        }
        lookup.town().clear(e.getTownUUID());
    }

    @EventHandler
    public void onNewDay(NewDayEvent e) {
        final StewardType treasurerType = StewardsAPI.getRegistry().getType(StewardTypeHandler.TREASURER_ID);
        final StewardType bailiffType = StewardsAPI.getRegistry().getType(StewardTypeHandler.BAILIFF_ID);
        final StewardType portType = StewardsAPI.getRegistry().getType(StewardTypeHandler.PORTMASTER_ID);
        final StewardType stableType = StewardsAPI.getRegistry().getType(StewardTypeHandler.STABLEMASTER_ID);
        if (treasurerType == null || bailiffType == null || portType == null || stableType == null)
            throw new IllegalStateException("Steward registry type was null");

        for (Town town : TownyAPI.getInstance().getTowns()) {
            int totalCost = 0;
            if (TownMetaData.NPC.has(town, portType)) {
                totalCost += Cfg.get().getInt("portmaster.daily-cost.level-" + TownMetaData.NPC.getStewardOptional(town, portType).map(Steward::getLevel).orElse(1));
            }
            if (TownMetaData.NPC.has(town, stableType)) {
                totalCost += Cfg.get().getInt("stablemaster.daily-cost.level-" + TownMetaData.NPC.getStewardOptional(town, portType).map(Steward::getLevel).orElse(1));
            }
            if (TownMetaData.NPC.has(town, treasurerType)) {
                totalCost += Cfg.get().getInt("treasurer.daily-cost.level-" + TownMetaData.NPC.getStewardOptional(town, portType).map(Steward::getLevel).orElse(1));
            }

            if (totalCost == 0) return;

            if (town.getAccount().canPayFromHoldings(totalCost)) {
                town.getAccount().withdraw(totalCost, "Stewards: Daily upkeep");
            } else {
                if (TownMetaData.NPC.has(town, portType)) {
                    TownMetaData.NPC.getStewardOptional(town, portType).ifPresent(steward -> {
                        steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).setStriking(true);
                        final Port port = PortsAPI.getPortFromTown(town);
                        if (port != null)
                            PortsAPI.setBlockaded(port, true);
                    });
                }
                if (TownMetaData.NPC.has(town, stableType)) {
                    TownMetaData.NPC.getStewardOptional(town, stableType).ifPresent(steward -> {
                        steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).setStriking(true);
                        final CarriageStation station = PortsAPI.getCarriageStationFromTown(town);
                        if (station != null)
                            PortsAPI.setBlockaded(station, true);
                    });
                }
                if (TownMetaData.NPC.has(town, treasurerType)) {
                    TownMetaData.NPC.getStewardOptional(town, treasurerType).ifPresent(steward -> {
                        steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).setStriking(true);
                        TownMetaData.setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-0"));
                    });
                }
            }
        }
    }

    @EventHandler
    public void onUnclaim(TownPreUnclaimEvent e) {
        if (e.getTown() == null)
            return;

        for (Steward steward : StewardsAPI.getStewards(e.getTown())) {
            final TownBlock townBlock = steward.getTownBlock();

            if (townBlock == null)
                continue;

            if (townBlock.equals(e.getTownBlock())) {
                e.setCancelMessage("There is a steward in this chunk. Move them to another chunk to unclaim this chunk.");
                e.setCancelled(true);
                return;
            }
        }
    }
}
