package io.github.milkdrinkers.stewards.listener;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to handle registration of event listeners.
 */
public class ListenerHandler implements Reloadable {
    private final Stewards plugin;
    private final List<Listener> listeners = new ArrayList<>();

    /**
     * Instantiates a the Listener handler.
     *
     * @param plugin the plugin instance
     */
    public ListenerHandler(Stewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(Stewards plugin) {
    }

    @Override
    public void onEnable(Stewards plugin) {
        listeners.clear(); // Clear the list to avoid duplicate listeners when reloading the plugin
        listeners.add(new TownyListener(plugin));
        listeners.add(new SettlersListener(plugin));
        listeners.add(new PlayerListener(plugin));

        // Register listeners here
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void onDisable(Stewards plugin) {
    }
}
