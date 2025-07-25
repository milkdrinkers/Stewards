package io.github.milkdrinkers.stewards.api;

import com.palmergames.bukkit.towny.object.Town;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardTypeRegistry;
import io.github.milkdrinkers.stewards.steward.lookup.ArchitectLookup;
import io.github.milkdrinkers.stewards.steward.lookup.StewardFollowLookup;
import io.github.milkdrinkers.stewards.steward.lookup.StewardLookup;
import io.github.milkdrinkers.stewards.steward.lookup.StewardTownLookup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class StewardsAPI {
    private static StewardsAPI INSTANCE;

    private static StewardsAPI getInstance() {
        if (INSTANCE == null)
            throw new RuntimeException("Stewards API was accessed before being initialized!");
        return INSTANCE;
    }

    protected static void setInstance(StewardsAPI instance) {
        INSTANCE = instance;
    }

    public abstract StewardTypeRegistry getRegistryInternal();

    public static StewardTypeRegistry getRegistry() {
        return getInstance().getRegistryInternal();
    }

    public abstract StewardLookup getLookupInternal();

    public static StewardLookup getLookup() {
        return getInstance().getLookupInternal();
    }

    public static @NotNull ArchitectLookup getLookupArchitect() {
        return getInstance().getLookupInternal().architect();
    }

    public static @NotNull StewardFollowLookup getLookupFollow() {
        return getInstance().getLookupInternal().follow();
    }

    public static @NotNull StewardTownLookup getLookupTown() {
        return getInstance().getLookupInternal().town();
    }

    public abstract Set<Steward> getTownStewards(@Nullable Town town);

    public static Set<Steward> getStewards(@Nullable Town town) {
        return getInstance().getTownStewards(town);
    }


}
