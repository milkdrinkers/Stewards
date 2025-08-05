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
import io.github.milkdrinkers.stewards.trait.traits.steward.ArchitectTrait;
import io.github.milkdrinkers.stewards.utility.Cfg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

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
            @SuppressWarnings("unused")
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

        steward.getTrait().setTownUUID(town.getUUID());
        steward.getTrait().hire();

        // If architect is outside claimed chunk, walk to town spawn (Which will be inside town)
        final Location architectLoc = steward.getTrait().getAnchorLocation();
        final Location townSpawnLoc = town.getSpawnOrNull();
        if (townSpawnLoc == null)
            return;

        if (!architectLoc.getChunk().equals(townSpawnLoc.getChunk())) {
            steward.walkToAnchor(townSpawnLoc);
        }
    }
}
