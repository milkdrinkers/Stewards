package io.github.milkdrinkers.stewards.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.settlers.api.SettlersAPI;
import io.github.milkdrinkers.settlers.api.enums.RemoveReason;
import io.github.milkdrinkers.settlers.api.event.settler.lifecycle.SettlerRemoveEvent;
import io.github.milkdrinkers.settlers.api.event.settler.lifetime.spawning.SettlerSpawnEvent;
import io.github.milkdrinkers.settlers.api.event.settlersapi.lifecycle.SettlersAPILoadedEvent;
import io.github.milkdrinkers.settlers.api.event.settlersapi.lifecycle.SettlersAPIUnloadedEvent;
import io.github.milkdrinkers.settlers.api.settler.AbstractSettler;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.steward.lookup.StewardLookup;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class SettlersListener implements Listener {
    private final Stewards plugin;
    private final @NotNull StewardLookup lookup;

    public SettlersListener(Stewards plugin) {
        this.plugin = plugin;
        this.lookup = plugin.getStewardLookup();
    }

    @EventHandler
    public void onSettlerLoad(SettlersAPILoadedEvent e) {
        for (NPC npc : SettlersAPI.getRegistryTown()) {
            final AbstractSettler settler = SettlersAPI.getSettler(npc);
            if (settler == null)
                continue;

            if (!settler.getNpc().hasTrait(StewardTrait.class))
                continue;

            if (lookup.get(settler) != null)
                continue;

            final StewardTrait stewardTrait = settler.getNpc().getOrAddTrait(StewardTrait.class);

            try {
                Steward steward = null;

                // If the Steward doesn't have at least one of these traits, something is wrong.
                if (settler.getNpc().hasTrait(ArchitectTrait.class)) {
                    // If the architect is not hired, i.e. town is not created, and more than 7 days have passed since the architect was first spawned, delete the architect
                    final Instant creationTime = settler.getNpc().getOrAddTrait(ArchitectTrait.class).getCreateTime();
                    final Instant deleteTime = creationTime.plus(Duration.ofDays(7));

                    if (!stewardTrait.isHired() && Instant.now().isAfter(deleteTime)) {
                        settler.delete();
                    } else {
                        steward = Steward.builder()
                            .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                                StewardTypeHandler.ARCHITECT_ID)))
                            .setDailyUpkeepCost(0)
                            .setIsEnabled(true)
                            .setIsHidden(false)
                            .setSettler(settler)
                            .build();

                        if (!stewardTrait.isHired())
                            lookup.architect().setArchitect(steward.getNpc().getOrAddTrait(ArchitectTrait.class).getSpawningPlayer(), steward);
                    }
                } else if (!stewardTrait.isHired()) { // For town stewards, if not hired delete
                    settler.delete();
                    if (stewardTrait.getTownUUID() != null) {
                        lookup.town().remove(stewardTrait.getTownUUID(), settler.getNpc().getUniqueId());
                        TownMetaData.setHiringSteward(stewardTrait.getTownUUID(), false);
                    }
                } else if (settler.getNpc().hasTrait(BailiffTrait.class)) {
                    steward = Steward.builder()
                        .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                            StewardTypeHandler.BAILIFF_ID)))
                        .setDailyUpkeepCost(0)
                        .setLevel(stewardTrait.getLevel())
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setSettler(settler)
                        .build();
                } else if (settler.getNpc().hasTrait(PortmasterTrait.class)) {
                    steward = Steward.builder()
                        .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                            StewardTypeHandler.PORTMASTER_ID)))
                        .setDailyUpkeepCost(0)
                        .setLevel(stewardTrait.getLevel())
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setSettler(settler)
                        .build();
                } else if (settler.getNpc().hasTrait(StablemasterTrait.class)) {
                    steward = Steward.builder()
                        .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                            StewardTypeHandler.STABLEMASTER_ID)))
                        .setDailyUpkeepCost(0)
                        .setLevel(stewardTrait.getLevel())
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setSettler(settler)
                        .build();
                } else if (settler.getNpc().hasTrait(TreasurerTrait.class)) {
                    steward = Steward.builder()
                        .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                            StewardTypeHandler.TREASURER_ID)))
                        .setDailyUpkeepCost(0)
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setSettler(settler)
                        .build();
                }

                if (steward != null) {
                    lookup.add(steward);

                    final UUID town = stewardTrait.getTownUUID();
                    if (town != null)
                        lookup.town().add(town, steward.getUniqueId());
                }
            } catch (InvalidStewardException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @EventHandler
    public void onSettlerUnload(SettlersAPIUnloadedEvent e) {
        lookup.architect().clear();
        lookup.town().clear();
        lookup.follow().clear();
        lookup.clear();
    }

    @EventHandler
    public void onSettlerSpawn(SettlerSpawnEvent e) {
        if (!e.getSettler().getNpc().hasTrait(StewardTrait.class))
            return;

        if (lookup.get(e.getSettler()) != null)
            return;

        final StewardTrait stewardTrait = e.getSettler().getNpc().getOrAddTrait(StewardTrait.class);

        // This theoretically shouldn't change anything, as the anchor location should always update as the NPC moves
        stewardTrait.setAnchorLocation(e.getLocation());

        try {
            Steward steward = null;

            // If the Steward doesn't have at least one of these traits, something is wrong.
            if (e.getSettler().getNpc().hasTrait(ArchitectTrait.class)) {
                // If the architect is not hired, i.e. town is not created, and more than 7 days have passed since the architect was first spawned, delete the architect
                final Instant creationTime = e.getSettler().getNpc().getOrAddTrait(ArchitectTrait.class).getCreateTime();
                final Instant deleteTime = creationTime.plus(Duration.ofDays(7));

                if (!stewardTrait.isHired() && Instant.now().isAfter(deleteTime)) {
                    e.getSettler().delete();
                } else {
                    steward = Steward.builder()
                        .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                            StewardTypeHandler.ARCHITECT_ID)))
                        .setDailyUpkeepCost(0)
                        .setIsEnabled(true)
                        .setIsHidden(false)
                        .setLevel(stewardTrait.getLevel())
                        .setSettler(e.getSettler())
                        .build();

                    lookup.architect().setArchitect(steward.getNpc().getOrAddTrait(ArchitectTrait.class).getSpawningPlayer(), steward);
                }
            } else if (!stewardTrait.isHired()) { // For town stewards, if not hired delete
                e.getSettler().delete();
                if (stewardTrait.getTownUUID() != null)
                    lookup.town().remove(stewardTrait.getTownUUID(), e.getSettler().getNpc().getUniqueId());
            } else if (e.getSettler().getNpc().hasTrait(BailiffTrait.class)) {
                steward = Steward.builder()
                    .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                        StewardTypeHandler.BAILIFF_ID)))
                    .setDailyUpkeepCost(0)
                    .setIsEnabled(true)
                    .setIsHidden(false)
                    .setLevel(stewardTrait.getLevel())
                    .setSettler(e.getSettler())
                    .build();
            } else if (e.getSettler().getNpc().hasTrait(PortmasterTrait.class)) {
                steward = Steward.builder()
                    .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                        StewardTypeHandler.PORTMASTER_ID)))
                    .setDailyUpkeepCost(0)
                    .setIsEnabled(true)
                    .setIsHidden(false)
                    .setLevel(stewardTrait.getLevel())
                    .setSettler(e.getSettler())
                    .build();
            } else if (e.getSettler().getNpc().hasTrait(StablemasterTrait.class)) {
                steward = Steward.builder()
                    .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                        StewardTypeHandler.STABLEMASTER_ID)))
                    .setDailyUpkeepCost(0)
                    .setIsEnabled(true)
                    .setIsHidden(false)
                    .setLevel(stewardTrait.getLevel())
                    .setSettler(e.getSettler())
                    .build();
            } else if (e.getSettler().getNpc().hasTrait(TreasurerTrait.class)) {
                steward = Steward.builder()
                    .setStewardType(Objects.requireNonNull(plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(
                        StewardTypeHandler.TREASURER_ID)))
                    .setDailyUpkeepCost(0)
                    .setIsEnabled(true)
                    .setIsHidden(false)
                    .setLevel(stewardTrait.getLevel())
                    .setSettler(e.getSettler())
                    .build();
            }

            if (steward != null) {
                lookup.add(steward);

                final UUID town = stewardTrait.getTownUUID();
                if (town != null)
                    lookup.town().add(town, steward.getUniqueId());
            }
        } catch (InvalidStewardException ex) {
            throw new RuntimeException(ex);
        }
    }

    @EventHandler
    public void onSettlerDelete(SettlerRemoveEvent e) {
        if (e.getReason() != RemoveReason.COMMAND) return;

        if (!e.getSettler().getNpc().hasTrait(StewardTrait.class)) return;

        // This should only run if a steward was deleted using commands, in which case the port/carriage station would not get removed - so we remove it.
        if (e.getSettler().getNpc().hasTrait(PortmasterTrait.class)) {
            PortsAPI.deleteAbstractPort(PortsAPI.getPortFromTown(TownyAPI.getInstance().getTown(lookup.get(e.getSettler()).getTownUUID())));
        } else if (e.getSettler().getNpc().hasTrait(StablemasterTrait.class)) {
            PortsAPI.deleteAbstractCarriageStation(PortsAPI.getCarriageStationFromTown(TownyAPI.getInstance().getTown(lookup.get(e.getSettler()).getTownUUID())));
        }

        // If the NPC is following a player, we clear that player's following state
        if (e.getSettler().getNpc().getTraitNullable(StewardTrait.class).isFollowing()) {
            lookup.follow().remove(e.getSettler().getNpc().getTraitNullable(StewardTrait.class).getFollowingPlayer());
        }

        // If architect and town hasn't been created, we remove that the player has an active architect
        if (e.getSettler().getNpc().hasTrait(ArchitectTrait.class) && !e.getSettler().getNpc().getTraitNullable(StewardTrait.class).isHired()) {
            lookup.architect().clearHasArchitect(e.getSettler().getNpc().getTraitNullable(ArchitectTrait.class).getSpawningPlayer());
        }

        // Uncache the Steward. We don't delete the Settler or NPC objects, as this is handled by their respective plugins
        lookup.remove(e.getSettler());
    }

    /**
     * Makes stewards follow the player that spawned them. (excludes architects as they are handled separately)
     * @param e event
     */
    @EventHandler
    public void onSettlerCreate(SettlerSpawnEvent e) {
        if (!e.getSettler().getNpc().hasTrait(StewardTrait.class))
            return;

        if (e.getSettler().getNpc().getTraitNullable(StewardTrait.class).getFollowingPlayer() == null)
            return;

        // Exclude architects as they have their own following logic
        if (e.getSettler().getNpc().hasTrait(ArchitectTrait.class))
            return;

        final Steward steward = lookup.get(e.getSettler());
        if (steward == null)
            return;

        steward.startFollowing(steward.getTrait().getFollowingPlayer());
    }
}
