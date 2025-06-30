package io.github.milkdrinkers.stewards.trait;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.trait.traits.*;
import io.github.milkdrinkers.stewards.trait.traits.guard.GuardCaptainTrait;
import io.github.milkdrinkers.stewards.trait.traits.guard.GuardTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;

public class TraitHandler implements Reloadable {
    @Override
    public void onLoad(Stewards plugin) {

    }

    @Override
    public void onEnable(Stewards plugin) {
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(StewardTrait.class).withName("steward"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ArchitectTrait.class).withName("architect"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BailiffTrait.class).withName("bailiff"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(PortmasterTrait.class).withName("portmaster"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(StablemasterTrait.class).withName("stablemaster"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TreasurerTrait.class).withName("treasurer"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ArchitectSpawnerTrait.class).withName("architectspawner"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(GuardTrait.class).withName("stewardsguard")); // Named "stewardsguard" as not to conflict with the GuardTrait in Settlers.
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(GuardCaptainTrait.class).withName("guardcaptain"));
    }

    @Override
    public void onDisable(Stewards plugin) {

    }
}
