package io.github.milkdrinkers.stewards.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.settlers.api.enums.RemoveReason;
import io.github.milkdrinkers.settlers.api.event.settler.lifecycle.SettlerCreateEvent;
import io.github.milkdrinkers.settlers.api.event.settler.lifecycle.SettlerRemoveEvent;
import io.github.milkdrinkers.settlers.api.event.settler.lifetime.spawning.SettlerSpawnEvent;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.trait.*;
import net.citizensnpcs.api.event.SpawnReason;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.time.Instant;

public class SettlersListener implements Listener {

    @EventHandler
    public void onSettlerSpawn(SettlerSpawnEvent e) {
        if (!e.getSettler().getNpc().hasTrait(StewardTrait.class)) return;

        if (StewardLookup.get().getSteward(e.getSettler()) != null) return;


        StewardTrait stewardTrait = e.getSettler().getNpc().getOrAddTrait(StewardTrait.class);

        // This theoretically shouldn't change anything, as the anchor location should always update as the NPC moves
        stewardTrait.setAnchorLocation(e.getLocation());



        // If the Steward doesn't have at least one of these traits, something is wrong.
        if (e.getSettler().getNpc().hasTrait(ArchitectTrait.class)) {

            // If the architect is not hired, i.e. town is not created, and more than 7 days have passed since the architect was first spawned, delete the architect
            if (!stewardTrait.isHired() &&
                Duration.between(e.getSettler().getNpc().getOrAddTrait(ArchitectTrait.class).getCreateTime(), Instant.now())
                    .compareTo(Duration.ofDays(7)) > 0) {
                e.getSettler().delete();
            } else {
                try {
                    Steward steward = Steward.builder()
                        .setStewardType(Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(
                            Stewards.getInstance().getStewardTypeHandler().ARCHITECT_ID))
                        .setDailyUpkeepCost(0)
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setLevel(stewardTrait.getLevel())
                        .setSettler(e.getSettler())
                        .build();

                    StewardLookup.get().registerSteward(steward);
                    StewardLookup.get().setArchitect(e.getSettler().getNpc().getOrAddTrait(ArchitectTrait.class).getSpawningPlayer(), steward);
                } catch (InvalidStewardException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (e.getSettler().getNpc().hasTrait(BailiffTrait.class)) {
            if (!stewardTrait.isHired()) {
                e.getSettler().delete();
            } else {
                try {
                    Steward steward = Steward.builder()
                        .setStewardType(Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(
                            Stewards.getInstance().getStewardTypeHandler().BAILIFF_ID))
                        .setDailyUpkeepCost(0)
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setLevel(stewardTrait.getLevel())
                        .setSettler(e.getSettler())
                        .build();

                    StewardLookup.get().registerSteward(steward);
                } catch (InvalidStewardException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (e.getSettler().getNpc().hasTrait(PortmasterTrait.class)) {
            if (!stewardTrait.isHired()) {
                e.getSettler().delete();
            } else {
                try {
                    Steward steward = Steward.builder()
                        .setStewardType(Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(
                            Stewards.getInstance().getStewardTypeHandler().PORTMASTER_ID))
                        .setDailyUpkeepCost(0)
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setLevel(stewardTrait.getLevel())
                        .setSettler(e.getSettler())
                        .build();

                    StewardLookup.get().registerSteward(steward);
                } catch (InvalidStewardException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (e.getSettler().getNpc().hasTrait(StablemasterTrait.class)) {
            if (!stewardTrait.isHired()) {
                e.getSettler().delete();
            } else {
                try {
                    Steward steward = Steward.builder()
                        .setStewardType(Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(
                            Stewards.getInstance().getStewardTypeHandler().STABLEMASTER_ID))
                        .setDailyUpkeepCost(0)
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setLevel(stewardTrait.getLevel())
                        .setSettler(e.getSettler())
                        .build();

                    StewardLookup.get().registerSteward(steward);
                } catch (InvalidStewardException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (e.getSettler().getNpc().hasTrait(TreasurerTrait.class)) {
            if (!stewardTrait.isHired()) {
                e.getSettler().delete();
            } else {
                try {
                    Steward steward = Steward.builder()
                        .setStewardType(Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(
                            Stewards.getInstance().getStewardTypeHandler().TREASURER_ID))
                        .setDailyUpkeepCost(0)
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setLevel(stewardTrait.getLevel())
                        .setSettler(e.getSettler())
                        .build();

                    StewardLookup.get().registerSteward(steward);
                } catch (InvalidStewardException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }


    }

    @EventHandler
    public void onSettlerDelete(SettlerRemoveEvent e) {
        if (e.getReason() != RemoveReason.COMMAND) return;

        if (!e.getSettler().getNpc().hasTrait(StewardTrait.class)) return;

        // This should only run if a steward was deleted using commands, in which case the port/carriage station would not get removed - so we remove it.
        if (e.getSettler().getNpc().hasTrait(PortmasterTrait.class)) {
            PortsAPI.deleteAbstractPort(PortsAPI.getPortFromTown(TownyAPI.getInstance().getTown(StewardLookup.get().getSteward(e.getSettler()).getTownUUID())));
        } else if (e.getSettler().getNpc().hasTrait(StablemasterTrait.class)) {
            PortsAPI.deleteAbstractCarriageStation(PortsAPI.getCarriageStationFromTown(TownyAPI.getInstance().getTown(StewardLookup.get().getSteward(e.getSettler()).getTownUUID())));
        }

        // If the NPC is following a player, we clear that player's following state
        if (e.getSettler().getNpc().getTraitNullable(StewardTrait.class).isFollowing()) {
            StewardLookup.get().removeStewardFollowingPlayer(e.getSettler().getNpc().getTraitNullable(StewardTrait.class).getFollowingPlayer());
        }

        // If architect and town hasn't been created, we remove that the player has an active architect
        if (e.getSettler().getNpc().hasTrait(ArchitectTrait.class) && !e.getSettler().getNpc().getTraitNullable(StewardTrait.class).isHired()) {
            StewardLookup.get().clearHasArchitect(e.getSettler().getNpc().getTraitNullable(ArchitectTrait.class).getSpawningPlayer());
        }

        // Uncache the Steward. We don't delete the Settler or NPC objects, as this is handled by their respective plugins
        StewardLookup.get().unregisterSteward(StewardLookup.get().getSteward(e.getSettler()));
    }

    @EventHandler
    public void onSettlerCreate(SettlerSpawnEvent e) {
        if (!e.getSettler().getNpc().hasTrait(StewardTrait.class))
            return;

        if (e.getSettler().getNpc().getTraitNullable(StewardTrait.class).getFollowingPlayer() == null)
            return;

        StewardLookup.get().getSteward(e.getSettler()).startFollowing(e.getSettler().getNpc().getTraitNullable(StewardTrait.class).getFollowingPlayer());
    }

}
