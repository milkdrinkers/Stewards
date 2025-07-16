package io.github.milkdrinkers.stewards.steward.lookup;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.steward.Steward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Stores a steward whenever it is following a player.
 */
public final class StewardFollowLookup implements Reloadable {
    private final HashMap<UUID, UUID> stewardFollowingPlayerHashmap = new HashMap<>(); // Player UUID to Steward UUID
    private final HashMap<UUID, UUID> playerFolloweByStewardHashmap = new HashMap<>(); // Steward UUID to Player UUID
    private final StewardLookup stewardLookup;

    StewardFollowLookup(StewardLookup stewardLookup) {
        this.stewardLookup = stewardLookup;
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
     * Removes a steward following a player entry from the lookup table.
     *
     * @param uuid the player uuid
     */
    public void remove(UUID uuid) {
        getFollower(uuid).ifPresent(steward -> playerFolloweByStewardHashmap.remove(steward.getUniqueId()));
        stewardFollowingPlayerHashmap.remove(uuid);
    }

    /**
     * Removes a steward following a player entry from the lookup table.
     *
     * @param player the player
     */
    public void remove(Player player) {
        remove(player.getUniqueId());
    }

    /**
     * Adds a steward following a player entry to the lookup table.
     *
     * @param uuid    the player uuid
     * @param steward the steward
     */
    public void add(UUID uuid, Steward steward) {
        stewardFollowingPlayerHashmap.put(uuid, steward.getUniqueId());
        playerFolloweByStewardHashmap.put(steward.getUniqueId(), uuid);
    }

    /**
     * Adds a steward following a player entry to the lookup table.
     *
     * @param player  the player
     * @param steward the steward
     */
    public void add(Player player, Steward steward) {
        add(player.getUniqueId(), steward);
    }

    /**
     * Check if a player is being followed by a steward.
     *
     * @param uuid the player uuid
     * @return true if followed, otherwise false
     */
    public boolean isFollowed(UUID uuid) {
        return stewardFollowingPlayerHashmap.containsKey(uuid);
    }

    /**
     * Check if a player is being followed by a steward.
     *
     * @param player the player
     * @return true if followed, otherwise false
     */
    public boolean isFollowed(Player player) {
        return isFollowed(player.getUniqueId());
    }

    /**
     * Get the steward following a player.
     *
     * @param uuid the player uuid
     * @return a steward or null
     */
    public Optional<Steward> getFollower(UUID uuid) {
        return Optional.ofNullable(stewardLookup.get(stewardFollowingPlayerHashmap.get(uuid)));
    }

    /**
     * Get the steward following a player.
     *
     * @param player the player
     * @return a steward or null
     */
    public Optional<Steward> getFollower(Player player) {
        return getFollower(player.getUniqueId());
    }

    /**
     * Get the player being followed by a steward.
     *
     * @param uuid the steward uuid
     * @return a player or null
     */
    public Optional<Player> getFollowee(UUID uuid) {
        return Optional.ofNullable(Bukkit.getPlayer(playerFolloweByStewardHashmap.get(uuid)));
    }

    /**
     * Get the player being followed by a steward.
     *
     * @param steward the steward
     * @return a player or null
     */
    public Optional<Player> getFollowee(Steward steward) {
        return getFollowee(steward.getUniqueId());
    }
}
