package io.github.milkdrinkers.stewards.trait.traits.steward;

import io.github.milkdrinkers.settlers.api.event.settler.lifetime.spawning.SettlerSpawnEvent;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.threadutil.Scheduler;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.UUID;

public class ArchitectTrait extends Trait {
    protected ArchitectTrait() {
        super("architect");
    }

    @Persist("spawningplayer")
    UUID spawningPlayer;
    @Persist("createtime")
    Instant createTime;

    public void load(DataKey key) {
    }

    public void save(DataKey key) {
    }

    public UUID getSpawningPlayer() {
        return spawningPlayer;
    }

    public void setSpawningPlayer(UUID spawningPlayer) {
        this.spawningPlayer = spawningPlayer;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    /**
     * Used to only spawn the architect NPC when the player who spawned it is online.
     * @param e event
     */
    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (getSpawningPlayer() == null)
            return;

        if (!e.getPlayer().getUniqueId().equals(getSpawningPlayer()))
            return;

        final Steward steward = StewardsAPI.getLookup().get(getNPC());
        if (steward == null)
            return;

        if (!steward.isFounder())
            return;

        if (!getNPC().isSpawned())
            getNPC().spawn(getNPC().getStoredLocation(), SpawnReason.PLUGIN);
    }

    /**
     * Used to only spawn the architect NPC when the player who spawned it is online.
     * @param e event
     */
    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (getSpawningPlayer() == null)
            return;

        if (!e.getPlayer().getUniqueId().equals(getSpawningPlayer()))
            return;

        final Steward steward = StewardsAPI.getLookup().get(getNPC());
        if (steward == null)
            return;

        if (!steward.isFounder())
            return;

        if (getNPC().isSpawned())
            getNPC().despawn(DespawnReason.PLUGIN);
    }

    /**
     * Handles spawning of the founding architect and resuming of player following.
     * @param e the SettlerSpawnEvent
     */
    @EventHandler
    @SuppressWarnings("unused")
    public void onSettlerSpawned(SettlerSpawnEvent e) {
        if (!e.getSettler().getNpc().equals(getNPC()))
            return;

        if (!e.getSettler().getNpc().hasTrait(StewardTrait.class))
            return;

        final Steward steward = StewardsAPI.getLookup().get(e.getSettler());
        if (steward == null)
            return;

        if (!steward.isFounder())
            return;

        // Handle resuming following if the steward is a founder
        final ArchitectTrait architectTrait = getNPC().getOrAddTrait(ArchitectTrait.class);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(architectTrait.getSpawningPlayer());
        if (!offlinePlayer.isOnline() && getNPC().isSpawned()) { // Server startup, owner is not online
            // Wait one tick to ensure the NPC is fully spawned (And this event is fired and processed) before despawning
            Scheduler.delay(1).sync(() -> {
                if (getNPC().isSpawned())
                    getNPC().despawn(DespawnReason.PLUGIN);
            }).execute();
        } else if (offlinePlayer.isOnline() && getNPC().isSpawned()) {
            steward.startFollowing(offlinePlayer.getPlayer());
        }
    }
}
