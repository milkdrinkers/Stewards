package io.github.milkdrinkers.stewards.steward;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.exception.InvalidStewardTypeException;
import io.github.milkdrinkers.stewards.trait.*;
import io.github.milkdrinkers.stewards.trait.traits.guard.GuardCaptainTrait;
import io.github.milkdrinkers.stewards.trait.traits.guard.GuardTrait;

public class StewardTypeHandler implements Reloadable {
    private StewardTypeRegistry stewardTypeRegistry;

    public final String ARCHITECT_ID = "architect";
    public final String TREASURER_ID = "treasurer";
    public final String BAILIFF_ID = "bailiff";
    public final String PORTMASTER_ID = "portmaster";
    public final String STABLEMASTER_ID = "stablemaster";
    public final String GUARDCAPTAIN_ID = "guardcaptain";
    public final String GUARD_ID = "guard";

    @Override
    public void onLoad(Stewards plugin) {
        stewardTypeRegistry = new StewardTypeRegistry();
        registerStewardTypes();
    }

    @Override
    public void onEnable(Stewards plugin) {

    }

    @Override
    public void onDisable(Stewards plugin) {
        stewardTypeRegistry.clear();
    }

    public StewardTypeRegistry getStewardTypeRegistry() {
        return stewardTypeRegistry;
    }

    private void registerStewardTypes() {
        try {
            StewardType architect = StewardType.builder()
                .setId(ARCHITECT_ID)
                .setMaxLevel(1)
                .setMinLevel(1)
                .setName("Architect")
                .setStartingLevel(1)
                .setSettlerPrefix("Architect") // TODO Translations
                .setTrait(ArchitectTrait.class)
                .build();

            stewardTypeRegistry.register(architect);

            StewardType treasurer = StewardType.builder()
                .setId(TREASURER_ID)
                .setMaxLevel(4)
                .setMinLevel(1)
                .setName("Treasurer")
                .setStartingLevel(1)
                .setSettlerPrefix("Treasurer") // TODO Translations
                .setTrait(TreasurerTrait.class)
                .build();

            stewardTypeRegistry.register(treasurer);

            StewardType bailiff = StewardType.builder()
                .setId(BAILIFF_ID)
                .setMaxLevel(3)
                .setMinLevel(1)
                .setName("Bailiff")
                .setStartingLevel(1)
                .setSettlerPrefix("Bailiff") // TODO Translations
                .setTrait(BailiffTrait.class)
                .build();

            stewardTypeRegistry.register(bailiff);

            StewardType portmaster = StewardType.builder()
                .setId(PORTMASTER_ID)
                .setMaxLevel(5)
                .setMinLevel(1)
                .setName("Port Master")
                .setStartingLevel(1)
                .setSettlerPrefix("Port Master") // TODO Translations
                .setTrait(PortmasterTrait.class)
                .build();

            stewardTypeRegistry.register(portmaster);

            StewardType stablemaster = StewardType.builder()
                .setId(STABLEMASTER_ID)
                .setMaxLevel(3)
                .setMinLevel(1)
                .setName("Stable Master")
                .setStartingLevel(1)
                .setSettlerPrefix("Stable Master") // TODO Translations
                .setTrait(StablemasterTrait.class)
                .build();

            stewardTypeRegistry.register(stablemaster);

            StewardType guardcaptain = StewardType.builder()
                .setId(GUARDCAPTAIN_ID)
                .setMaxLevel(4)
                .setMinLevel(1)
                .setName("Guard Captain")
                .setStartingLevel(1)
                .setSettlerPrefix("Guard Captain")
                .setTrait(GuardCaptainTrait.class)
                .build();

            stewardTypeRegistry.register(guardcaptain);

            StewardType guard = StewardType.builder()
                .setId(GUARD_ID)
                .setMaxLevel(1)
                .setMinLevel(1)
                .setName("Guard")
                .setStartingLevel(1)
                .setSettlerPrefix("Guard")
                .setTrait(GuardTrait.class)
                .build();
        } catch (InvalidStewardTypeException e) {
            throw new RuntimeException(e);
        }
    }
}
