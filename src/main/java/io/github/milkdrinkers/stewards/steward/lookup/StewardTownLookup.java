package io.github.milkdrinkers.stewards.steward.lookup;

import com.palmergames.bukkit.towny.object.Town;
import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.steward.Steward;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tracks which stewards belong to which towns.
 */
public final class StewardTownLookup implements Reloadable {
    private final HashMap<UUID, Set<UUID>> townStewardUuidMap = new HashMap<>(); // Town UUID to Set<Steward UUID>
    private final StewardLookup stewardLookup;

    public StewardTownLookup(StewardLookup stewardLookup) {
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

    public Set<Steward> getTownStewards(UUID uuid) {
        if (townStewardUuidMap.isEmpty()) {
            return Set.of();
        } else {
            return townStewardUuidMap.get(uuid)
                .stream()
                .map(stewardLookup::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }
    }

    public Set<Steward> getTownStewards(Town town) {
        return getTownStewards(town.getUUID());
    }

    public void add(UUID townUuid, UUID stewardUuid) {
        townStewardUuidMap.computeIfAbsent(townUuid, k -> new HashSet<>()).add(stewardUuid);
    }

    public void add(Town town, Steward steward) {
        add(town.getUUID(), steward.getUniqueId());
    }

    public void remove(UUID townUuid, UUID stewardUuid) {
        townStewardUuidMap.computeIfPresent(townUuid, (key, uuids) -> {
            uuids.remove(stewardUuid);
            return uuids.isEmpty() ? null : uuids; // Removes the entire town entry if uuids is empty
        });
    }

    public void remove(Town town, Steward steward) {
        remove(town.getUUID(), steward.getUniqueId());
    }

    public void clear(UUID townUuid) {
        townStewardUuidMap.remove(townUuid);
    }

    public void clear(Town town) {
        clear(town.getUUID());
    }

    public void clear() {
        townStewardUuidMap.clear();
    }
}
