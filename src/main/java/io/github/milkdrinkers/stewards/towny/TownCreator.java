package io.github.milkdrinkers.stewards.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.command.TownCommand;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.lookup.StewardLookup;
import io.github.milkdrinkers.stewards.trait.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Cfg;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class TownCreator {
    public static void create(
        Player player,
        String townName,
        Steward steward
    ) throws TownyException {
        final Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null)
            throw new IllegalStateException("Resident was null when creating town!");

        // Use one time event listener to reacto to town creation using architects
        final Listener listener = new Listener() {
            @EventHandler
            public void onNewTown(NewTownEvent e) {
                if (e.getTown().getMayor().equals(resident)) {
                    HandlerList.unregisterAll(this); // Unregister self on success
                    processCreation(player, e.getTown(), steward);
                }
            }
        };

        try {
            Bukkit.getPluginManager().registerEvents(listener, Stewards.getInstance());
            TownCommand.newTown(player, townName, resident, false);
        } catch (TownyException e) {
            HandlerList.unregisterAll(listener);
            throw e; // Rethrow exception to send player fail message
        }
    }

    private static void processCreation(Player player, Town town, Steward steward) {
        final StewardLookup lookup = StewardsAPI.getLookup();

        steward.stopFollowing(player, true);

        lookup.architect().clearHasArchitect(steward.getSettler().getNpc().getOrAddTrait(ArchitectTrait.class).getSpawningPlayer());

        TownMetaData.setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-0"));

        TownMetaData.NPC.set(town, steward);

        lookup.town().add(town, steward);

        steward.setTownUUID(town.getUUID());
        steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).hire();
        steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).setTownUUID(town.getUUID());

        if (TownyAPI.getInstance().getTown(steward.getSettler().getNpc().getEntity().getLocation()).getUUID() == null && TownyAPI.getInstance().getTown(steward.getSettler().getNpc().getEntity().getLocation()).getUUID() != steward.getTownUUID()) {
            try {
                steward.getSettler().getNpc().teleport(town.getSpawn(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).setAnchorLocation(steward.getSettler().getNpc().getEntity().getLocation());
                steward.stopFollowing(steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).getFollowingPlayer());
                steward.getSettler().getNpc().getNavigator().setTarget(steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).getAnchorLocation());
            } catch (TownyException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
