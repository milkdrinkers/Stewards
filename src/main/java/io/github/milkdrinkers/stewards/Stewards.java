package io.github.milkdrinkers.stewards;

import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.stewards.command.CommandHandler;
import io.github.milkdrinkers.stewards.config.ConfigHandler;
import io.github.milkdrinkers.stewards.hook.HookManager;
import io.github.milkdrinkers.stewards.listener.ListenerHandler;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.threadutil.SchedulerHandler;
import io.github.milkdrinkers.stewards.trait.*;
import io.github.milkdrinkers.stewards.translation.TranslationHandler;
import io.github.milkdrinkers.stewards.updatechecker.UpdateHandler;
import io.github.milkdrinkers.stewards.utility.Logger;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Main class.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class Stewards extends JavaPlugin {
    private static Stewards instance;

    // Handlers/Managers
    private ConfigHandler configHandler;
    private TranslationHandler translationHandler;
    private HookManager hookManager;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;
    private UpdateHandler updateHandler;
    private SchedulerHandler schedulerHandler;
    // Steward handlers
    private StewardTypeHandler stewardTypeHandler;
    private StewardLookup stewardLookup;

    // Handlers list (defines order of load/enable/disable)
    private List<? extends Reloadable> handlers;

    /**
     * Gets plugin instance.
     *
     * @return the plugin instance
     */
    public static Stewards getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;


        configHandler = new ConfigHandler(this);
        translationHandler = new TranslationHandler(configHandler);
        hookManager = new HookManager(this);
        stewardTypeHandler = new StewardTypeHandler();
        stewardLookup = new StewardLookup(this);
        commandHandler = new CommandHandler(this);
        listenerHandler = new ListenerHandler(this);
        updateHandler = new UpdateHandler(this);
        schedulerHandler = new SchedulerHandler();

        handlers = List.of(
            configHandler,
            translationHandler,
            hookManager,
            stewardTypeHandler,
            stewardLookup,
            commandHandler,
            listenerHandler,
            updateHandler,
            schedulerHandler
        );

        for (Reloadable handler : handlers)
            handler.onLoad(instance);
    }

    @Override
    public void onEnable() {
        for (Reloadable handler : handlers)
            handler.onEnable(instance);

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(StewardTrait.class).withName("steward"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ArchitectTrait.class).withName("architect"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(BailiffTrait.class).withName("bailiff"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(PortmasterTrait.class).withName("portmaster"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(StablemasterTrait.class).withName("stablemaster"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TreasurerTrait.class).withName("treasurer"));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ArchitectSpawnerTrait.class).withName("architectspawner"));
    }

    @Override
    public void onDisable() {
        for (Reloadable handler : handlers.reversed()) // If reverse doesn't work implement a new List with your desired disable order
            handler.onDisable(instance);
    }

    /**
     * Use to reload the entire plugin.
     */
    public void onReload() {
        onDisable();
        onLoad();
        onEnable();
    }

    /**
     * Gets config handler.
     *
     * @return the config handler
     */
    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    /**
     * Gets hook manager.
     *
     * @return the hook manager
     */
    @NotNull
    public HookManager getHookManager() {
        return hookManager;
    }

    /**
     * Gets update handler.
     *
     * @return the update handler
     */
    @NotNull
    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }

    /**
     * Gets StewardType handler.
     *
     * @return the StewardType handler
     */
    @NotNull
    public StewardTypeHandler getStewardTypeHandler() {
        return stewardTypeHandler;
    }

    /**
     * Gets StewardLookup instance
     */
    @NotNull
    public StewardLookup getStewardLookup() {
        return stewardLookup;
    }
}
