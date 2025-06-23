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
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.transaction.TransactionType;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.towny.TownyDataUtil;
import io.github.milkdrinkers.stewards.trait.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.stewards.utility.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TownyListener implements Listener {

    @EventHandler
    public void onTownyDeposit(TownPreTransactionEvent e) {
        if (e.getTransaction().getType() != TransactionType.DEPOSIT) return;

        if (e.getTransaction().getSendingPlayer() == null) return;

        Town town = e.getTown();

        if ((e.getTransaction().getReceivingAccount().getHoldingBalance() + e.getTransaction().getAmount()) > TownMetaData.getBankLimit(town)) {
            e.setCancelMessage("You can't transfer that much money into your town bank. Your town bank limit is: " + TownMetaData.getBankLimit(town));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTownStatusScreen(TownStatusScreenEvent e) {
        Component hoverComponent;

        if (TownMetaData.hasTreasurer(e.getTown())) {
            hoverComponent = Component.text("Your Treasurer is level "
                + StewardLookup.get().getSteward(TownMetaData.getTreasurer(e.getTown())).getLevel()
                + ". To increase this limit, upgrade your Treasurer.", NamedTextColor.GRAY);
        } else {
            hoverComponent = Component.text("You don't have a Treasurer. To increase this limit, hire a Treasurer.", NamedTextColor.GRAY);
        }

        Component bankLimit = Component.newline()
            .append(Component.text("[", NamedTextColor.GRAY))
            .append(Component.text("Stewards", NamedTextColor.DARK_GREEN))
            .append(Component.text("] ", NamedTextColor.GRAY))
            .append(Component.text("Town bank limit: %s⊚.".formatted(TownMetaData.getBankLimit(e.getTown())), NamedTextColor.WHITE))
            .hoverEvent(HoverEvent.showText(hoverComponent));

        e.getStatusScreen().addComponentOf("Stewards", bankLimit);
    }

    @EventHandler
    public void onNewTown(NewTownEvent e) {
        Town town = e.getTown();

        if (!TownyDataUtil.isStewardCreatedTown(town.getMayor().getUUID())) return;

        Steward steward = StewardLookup.get().getSteward(TownyDataUtil.getStewardUUID(town.getMayor().getUUID()));

        StewardLookup.get().clearHasArchitect(steward.getSettler().getNpc().getOrAddTrait(ArchitectTrait.class).getSpawningPlayer());

        TownMetaData.setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-0"));

        TownMetaData.setArchitect(town, steward);

        StewardLookup.get().addStewardUuidToTown(town, steward);

        steward.setTownUUID(town.getUUID());
        steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).hire();
        steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).setTownUUID(town.getUUID());

        try {
            steward.setTownBlock(town.getHomeBlock());
            steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).setTownBlock(town.getHomeBlock());
        } catch (TownyException ex) {
            Logger.get().error("Couldn't set steward townblock when creating town: " + ex);
        }

        if (TownyAPI.getInstance().getTown(steward.getSettler().getNpc().getEntity().getLocation()).getUUID() == null
            && TownyAPI.getInstance().getTown(steward.getSettler().getNpc().getEntity().getLocation()).getUUID() != steward.getTownUUID()) {
            try {
                steward.getSettler().getNpc().teleport(town.getSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).setAnchorLocation(steward.getSettler().getNpc().getEntity().getLocation());
                steward.stopFollowing(steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).getFollowingPlayer());
                steward.getSettler().getNpc().getNavigator().setTarget(steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).getAnchorLocation());
            } catch (TownyException ex) {
                throw new RuntimeException(ex);
            }
        }

        TownyDataUtil.removePlayerAndSteward(town.getMayor().getUUID());
    }

    @EventHandler
    public void onTownRemoved(DeleteTownEvent e) {
        StewardLookup.get().getTownStewardUuids(e.getTownUUID()).forEach(stewardUuid -> {
            StewardLookup.get().getSteward(stewardUuid).getSettler().delete();
            StewardLookup.get().unregisterSteward(stewardUuid);
        });
    }

    @EventHandler
    public void onNewDay(NewDayEvent e) {
        for (Town town : TownyAPI.getInstance().getTowns()) {
            int totalCost = 0;
            if (TownMetaData.hasPortmaster(town)) {
                totalCost += Cfg.get().getInt("portmaster.daily-cost.level-" + StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getLevel());
            }
            if (TownMetaData.hasStablemaster(town)) {
                totalCost += Cfg.get().getInt("stablemaster.daily-cost.level-" + StewardLookup.get().getSteward(TownMetaData.getStablemaster(town)).getLevel());
            }
            if (TownMetaData.hasTreasurer(town)) {
                totalCost += Cfg.get().getInt("treasurer.daily-cost.level-" + StewardLookup.get().getSteward(TownMetaData.getTreasurer(town)).getLevel());
            }

            if (totalCost == 0) return;

            if (town.getAccount().canPayFromHoldings(totalCost)) {
                town.getAccount().withdraw(totalCost, "Stewards: Daily upkeep");
            } else {
                if (TownMetaData.hasPortmaster(town)) {
                    StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).setStriking(true);
                    PortsAPI.setBlockaded(PortsAPI.getPortFromTown(town), true);
                }
                if (TownMetaData.hasStablemaster(town)) {
                    StewardLookup.get().getSteward(TownMetaData.getStablemaster(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).setStriking(true);
                    PortsAPI.setBlockaded(PortsAPI.getCarriageStationFromTown(town), true);
                }
                if (TownMetaData.hasTreasurer(town)) {
                    StewardLookup.get().getSteward(TownMetaData.getTreasurer(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).setStriking(true);
                    TownMetaData.setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-0"));
                }
            }
        }
    }

    @EventHandler
    public void onUnclaim(TownPreUnclaimEvent e) {
        if (TownMetaData.hasArchitect(e.getTown())) {
            if (StewardLookup.get().getSteward(TownMetaData.getArchitect(e.getTown())).getTownBlock().getWorldCoord().equals(e.getTownBlock().getWorldCoord())) {
                e.setCancelMessage("There is a steward in this chunk. Move them to another chunk to unclaim this chunk.");
                e.setCancelled(true);
            }
        }

        if (TownMetaData.hasBailiff(e.getTown())) {
            if (StewardLookup.get().getSteward(TownMetaData.getBailiff(e.getTown())).getTownBlock().getWorldCoord().equals(e.getTownBlock().getWorldCoord())) {
                e.setCancelMessage("There is a steward in this chunk. Move them to another chunk to unclaim this chunk.");
                e.setCancelled(true);
            }
        }


        if (TownMetaData.hasPortmaster(e.getTown())) {
            if (StewardLookup.get().getSteward(TownMetaData.getPortmaster(e.getTown())).getTownBlock().getWorldCoord().equals(e.getTownBlock().getWorldCoord())) {
                e.setCancelMessage("There is a steward in this chunk. Move them to another chunk to unclaim this chunk.");
                e.setCancelled(true);
            }
        }

        if (TownMetaData.hasStablemaster(e.getTown())) {
            if (StewardLookup.get().getSteward(TownMetaData.getStablemaster(e.getTown())).getTownBlock().getWorldCoord().equals(e.getTownBlock().getWorldCoord())) {
                e.setCancelMessage("There is a steward in this chunk. Move them to another chunk to unclaim this chunk.");
                e.setCancelled(true);
            }
        }


        if (TownMetaData.hasTreasurer(e.getTown())) {
            if (StewardLookup.get().getSteward(TownMetaData.getTreasurer(e.getTown())).getTownBlock().getWorldCoord().equals(e.getTownBlock().getWorldCoord())) {
                e.setCancelMessage("There is a steward in this chunk. Move them to another chunk to unclaim this chunk.");
                e.setCancelled(true);
            }
        }
    }
}
