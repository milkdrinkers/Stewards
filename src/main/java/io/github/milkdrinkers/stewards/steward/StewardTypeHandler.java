package io.github.milkdrinkers.stewards.steward;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.exception.InvalidStewardTypeException;

public class StewardTypeHandler implements Reloadable {

    private StewardTypeRegistry stewardTypeRegistry;

    public String ARCHITECT_ID = "architect";
    public String TREASURER_ID = "treasurer";
    public String BAILIFF_ID = "bailiff";
    public String PORTMASTER_ID = "portmaster";
    public String STABLEMASTER_ID = "stablemaster";
    public String GUARDCAPTAIN_ID = "guardcaptain";
    public String GUARD_ID = "guard";

    @Override
    public void onLoad(Stewards plugin) {
        stewardTypeRegistry = new StewardTypeRegistry();
    }

    @Override
    public void onEnable(Stewards plugin) {
        stewardTypeRegistry.clear();
        registerStewardTypes();
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
                .build();

            stewardTypeRegistry.register(architect);

            StewardType treasurer = StewardType.builder()
                .setId(TREASURER_ID)
                .setMaxLevel(4)
                .setMinLevel(1)
                .setName("Treasurer")
                .setStartingLevel(1)
                .setSettlerPrefix("Treasurer") // TODO Translations
                .build();

            stewardTypeRegistry.register(treasurer);

            StewardType bailiff = StewardType.builder()
                .setId(BAILIFF_ID)
                .setMaxLevel(3)
                .setMinLevel(1)
                .setName("Bailiff")
                .setStartingLevel(1)
                .setSettlerPrefix("Bailiff") // TODO Translations
                .build();

            stewardTypeRegistry.register(bailiff);

            StewardType portmaster = StewardType.builder()
                .setId(PORTMASTER_ID)
                .setMaxLevel(5)
                .setMinLevel(1)
                .setName("Port Master")
                .setStartingLevel(1)
                .setSettlerPrefix("Port Master") // TODO Translations
                .build();

            stewardTypeRegistry.register(portmaster);

            StewardType stablemaster = StewardType.builder()
                .setId(STABLEMASTER_ID)
                .setMaxLevel(3)
                .setMinLevel(1)
                .setName("Stable Master")
                .setStartingLevel(1)
                .setSettlerPrefix("Stable Master") // TODO Translations
                .build();

            stewardTypeRegistry.register(stablemaster);

            StewardType guardcaptain = StewardType.builder()
                .setId(GUARDCAPTAIN_ID)
                .setMaxLevel(4)
                .setMinLevel(1)
                .setName("Guard Captain")
                .setStartingLevel(1)
                .setSettlerPrefix("Guard Captain")
                .build();

            stewardTypeRegistry.register(guardcaptain);

            StewardType guard = StewardType.builder()
                .setId(GUARD_ID)
                .setMaxLevel(1)
                .setMinLevel(1)
                .setName("Guard")
                .setStartingLevel(1)
                .setSettlerPrefix("Guard")
                .build();
        } catch (InvalidStewardTypeException e) {
            throw new RuntimeException(e);
        }
    }
}
