package io.github.milkdrinkers.stewards;

import com.palmergames.bukkit.towny.object.Town;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardTypeRegistry;
import io.github.milkdrinkers.stewards.steward.lookup.StewardLookup;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

public class StewardsAPIProvider extends StewardsAPI implements Reloadable {
    private final Stewards plugin;

    StewardsAPIProvider(Stewards plugin) {
        super();
        this.plugin = plugin;
        setInstance(this);
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

    @Override
    public StewardTypeRegistry getRegistryInternal() {
        return plugin.getStewardTypeHandler().getStewardTypeRegistry();
    }

    @Override
    public StewardLookup getLookupInternal() {
        return plugin.getStewardLookup();
    }

    @Override
    public Set<Steward> getTownStewards(@Nullable Town town) {
        if (town == null)
            return Set.of();

        return TownMetaData.NPC.getStewards(town)
            .stream()
            .map(uuid -> plugin.getStewardLookup().get(uuid))
            .collect(Collectors.toSet());
    }
}
