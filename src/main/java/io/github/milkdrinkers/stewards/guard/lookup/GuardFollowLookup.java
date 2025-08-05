package io.github.milkdrinkers.stewards.guard.lookup;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.guard.Guard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;


/**
 * Stores a guard whenever it is following a player.
 */
public final class GuardFollowLookup implements Reloadable {
    private final HashMap<UUID, UUID> guardFollowingPlayerHashmap = new HashMap<>(); // Player UUID to Guard UUID
    private final HashMap<UUID, UUID> playerFolloweByGuardHashmap = new HashMap<>(); // Guard UUID to Player UUID
    private final GuardLookup guardLookup;

    GuardFollowLookup(GuardLookup guardLookup) {
        this.guardLookup = guardLookup;
    }

    @Override
    public void onLoad(Stewards plugin) {
    }

    @Override
    public void onEnable(Stewards plugin) {
    }

    @Override
    public void onDisable(Stewards plugin) {
    }

    /**
     * Removes a guard following a player entry from the lookup table.
     *
     * @param uuid the player uuid
     */
    public void remove(UUID uuid) {
        getFollower(uuid).ifPresent(steward -> playerFolloweByGuardHashmap.remove(steward.getUniqueId()));
        guardFollowingPlayerHashmap.remove(uuid);
    }

    /**
     * Removes a guard following a player entry from the lookup table.
     *
     * @param player the player
     */
    public void remove(Player player) {
        remove(player.getUniqueId());
    }

    /**
     * Adds a guard following a player entry to the lookup table.
     *
     * @param uuid    the player uuid
     * @param guard the guard
     */
    public void add(UUID uuid, Guard guard) {
        guardFollowingPlayerHashmap.put(uuid, guard.getUniqueId());
        playerFolloweByGuardHashmap.put(guard.getUniqueId(), uuid);
    }

    /**
     * Adds a guard following a player entry to the lookup table.
     *
     * @param player  the player
     * @param guard the guard
     */
    public void add(Player player, Guard guard) {
        add(player.getUniqueId(), guard);
    }

    /**
     * Check if a player is being followed by a guard.
     *
     * @param uuid the player uuid
     * @return true if followed, otherwise false
     */
    public boolean isFollowed(UUID uuid) {
        return guardFollowingPlayerHashmap.containsKey(uuid);
    }

    /**
     * Check if a player is being followed by a guard.
     *
     * @param player the player
     * @return true if followed, otherwise false
     */
    public boolean isFollowed(Player player) {
        return isFollowed(player.getUniqueId());
    }

    /**
     * Get the guard following a player.
     *
     * @param uuid the player uuid
     * @return a guard or null
     */
    public Optional<Guard> getFollower(UUID uuid) {
        return Optional.ofNullable(guardLookup.get(guardFollowingPlayerHashmap.get(uuid)));
    }

    /**
     * Get the guard following a player.
     *
     * @param player the player
     * @return a guard or null
     */
    public Optional<Guard> getFollower(Player player) {
        return getFollower(player.getUniqueId());
    }

    /**
     * Get the player being followed by a guard.
     *
     * @param uuid the guard uuid
     * @return a player or null
     */
    public Optional<Player> getFollowee(UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(playerFolloweByGuardHashmap.get(uuid)));
    }

    /**
     * Get the player being followed by a guard.
     *
     * @param guard the guard
     * @return a player or null
     */
    public Optional<Player> getFollowee(Guard guard) {
        return getFollowee(guard.getUniqueId());
    }

    public void clear() {
        playerFolloweByGuardHashmap.clear();
        guardFollowingPlayerHashmap.clear();
    }
}
