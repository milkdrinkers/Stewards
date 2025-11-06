package io.github.milkdrinkers.stewards.config;

import io.github.milkdrinkers.crate.Config;
import io.github.milkdrinkers.crate.internal.settings.ReloadSetting;
import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;

/**
 * A class that generates/loads {@literal &} provides access to a configuration file.
 */
public class ConfigHandler implements Reloadable {
    private final Stewards plugin;
    private Config cfg;
    private Config nameCfg;
    private Config skinsCfg;

    /**
     * Instantiates a new Config handler.
     *
     * @param plugin the plugin instance
     */
    public ConfigHandler(Stewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(Stewards plugin) {
        cfg = Config.builderConfig()
            .path(plugin.getDataFolder().toPath().resolve("config.yml"))
            .defaults(plugin.getResource("config.yml"))
            .reload(ReloadSetting.MANUALLY)
            .build();
        nameCfg = Config.builderConfig()
            .path(plugin.getDataFolder().toPath().resolve("names.yml"))
            .defaults(plugin.getResource("names.yml"))
            .reload(ReloadSetting.MANUALLY)
            .build();
        skinsCfg = Config.builderConfig()
            .path(plugin.getDataFolder().toPath().resolve("skins.yml"))
            .defaults(plugin.getResource("skins.yml"))
            .reload(ReloadSetting.MANUALLY)
            .build();
    }

    @Override
    public void onEnable(Stewards plugin) {
    }

    @Override
    public void onDisable(Stewards plugin) {
    }

    /**
     * Gets main config object.
     *
     * @return the config object
     */
    public Config getConfig() {
        return cfg;
    }

    /**
     * Gets name config object.
     *
     * @return the config object
     */
    public Config getNameCfg() {
        return nameCfg;
    }

    public Config getSkinsCfg() {
        return skinsCfg;
    }
}
