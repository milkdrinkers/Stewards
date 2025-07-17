package io.github.milkdrinkers.stewards.steward.lookup;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.steward.Steward;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Stores which architect belongs to which player.
 */
public final class ArchitectLookup implements Reloadable {
    private final HashMap<UUID, UUID> architectMap = new HashMap<>(); // Player UUID to Steward UUID
    private final StewardLookup stewardLookup;

    public ArchitectLookup(StewardLookup stewardLookup) {
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

    public void setArchitect(UUID uuid, Steward steward) {
        architectMap.put(uuid, steward.getUniqueId());
    }

    public void setArchitect(Player player, Steward steward) {
        setArchitect(player.getUniqueId(), steward);
    }

    public void clearHasArchitect(UUID uuid) {
        architectMap.remove(uuid);
    }

    public void clearHasArchitect(Player player) {
        clearHasArchitect(player.getUniqueId());
    }

    public boolean hasArchitect(UUID uuid) {
        return architectMap.containsKey(uuid);
    }

    public boolean hasArchitect(Player player) {
        return hasArchitect(player.getUniqueId());
    }

    public Steward getArchitect(UUID uuid) {
        return stewardLookup.get(architectMap.get(uuid));
    }

    public Steward getArchitect(Player player) {
        return getArchitect(player.getUniqueId());
    }

    public void clear() {
        architectMap.clear();
    }
}
