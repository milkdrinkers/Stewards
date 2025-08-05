package io.github.milkdrinkers.stewards.guard.lookup;

import com.palmergames.bukkit.towny.object.Town;
import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.guard.Guard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tracks which guards belong to which towns.
 */
public final class GuardTownLookup implements Reloadable {
    private final HashMap<UUID, Set<UUID>> townGuardUuidMap = new HashMap<>(); // Town UUID to Set<Guard UUID>
    private final GuardLookup guardLookup;

    public GuardTownLookup(GuardLookup guardLookup) {
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

    public Set<Guard> getTownGuards(UUID uuid) {
        if (townGuardUuidMap.isEmpty()) {
            return Set.of();
        } else {
            return townGuardUuidMap.get(uuid)
                .stream()
                .map(guardLookup::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }
    }

    public Set<Guard> getTownGuards(Town town) {
        return getTownGuards(town.getUUID());
    }

    public void add(UUID townUuid, UUID guardUuid) {
        townGuardUuidMap.computeIfAbsent(townUuid, k -> new HashSet<>()).add(guardUuid);
    }

    public void add(Town town, Guard guard) {
        add(town.getUUID(), guard.getUniqueId());
    }

    public void remove(UUID townUuid, UUID guardUuid) {
        townGuardUuidMap.computeIfPresent(townUuid, (key, uuids) -> {
            uuids.remove(guardUuid);
            return uuids.isEmpty() ? null : uuids; // Removes the entire town entry if uuids is empty
        });
    }

    public void remove(Town town, Guard guard) {
        remove(town.getUUID(), guard.getUniqueId());
    }

    public void clear(UUID townUuid) {
        townGuardUuidMap.remove(townUuid);
    }

    public void clear(Town town) {
        clear(town.getUUID());
    }

    public void clear() {
        townGuardUuidMap.clear();
    }
}
