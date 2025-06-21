package io.github.milkdrinkers.stewards.config;

import io.github.milkdrinkers.crate.Config;
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
        cfg = new Config("config", plugin.getDataFolder().getPath(), plugin.getResource("config.yml")); // Create a config file from the template in our resources folder
        nameCfg = new Config("names", plugin.getDataFolder().getPath(), plugin.getResource("names.yml"));
        skinsCfg = new Config("skins", plugin.getDataFolder().getPath(), plugin.getResource("skins.yml"));
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
