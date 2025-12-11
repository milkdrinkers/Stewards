package io.github.milkdrinkers.stewards.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewDayEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.PreDeleteTownEvent;
import com.palmergames.bukkit.towny.event.economy.TownPreTransactionEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimCmdEvent;
import com.palmergames.bukkit.towny.event.town.TownPreUnclaimEvent;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.palmergames.bukkit.towny.object.economy.transaction.TransactionType;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.alathra.alathraports.core.carriagestations.CarriageStation;
import io.github.alathra.alathraports.core.ports.Port;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
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
import io.github.milkdrinkers.threadutil.Scheduler;
import net.kyori.adventure.text.Component;
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
            hoverComponent = ColorParser.of("<gray>Your treasurer is level <level>. To increase this limit, upgrade your treasurer.")
                .with("level", String.valueOf(TownMetaData.NPC.getStewardOptional(e.getTown(), type).map(Steward::getLevel).orElse(0))).build(); // TODO Translate
        } else {
            hoverComponent = ColorParser.of("<gray>You don't have a treasurer. To increase this limit, hire a treasurer.").build(); // TODO Translate
        }

        Component bankLimit = Component.newline()
            .append((ColorParser.of("<gray>[<green>Stewards<gray>] <white>Town bank limit: <limit>")
                .with("limit", String.valueOf(TownMetaData.getBankLimit(e.getTown()))))
                .build()); // TODO Translate

        e.getStatusScreen().addComponentOf("Stewards", bankLimit);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTownRemoved(DeleteTownEvent e) {
        Scheduler.sync(() -> {
            for (final Steward steward : lookup.town().getTownStewards(e.getTownUUID())) {
                DeleteUtils.dismiss(steward, e.getTownUUID(), null, false);
            }
            lookup.town().clear(e.getTownUUID());
        }).execute();
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
    public void onUnclaim(TownPreUnclaimCmdEvent e) {
        if (e.getTown() == null)
            return;

        for (final Steward steward : StewardsAPI.getStewards(e.getTown())) {
            final TownBlock townBlock = steward.getTownBlock();

            if (townBlock == null)
                continue;

            final WorldCoord wc = townBlock.getWorldCoord();

            for (WorldCoord worldCoord : e.getUnclaimSelection()) {
                if (wc.equals(worldCoord)) {
                    e.setCancelMessage("There is a steward in one of the chunks you are trying to unclaim. Move them to another chunk to unclaim.");
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
