package io.github.milkdrinkers.stewards;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;
import io.github.milkdrinkers.stewards.command.CommandHandler;
import io.github.milkdrinkers.stewards.config.ConfigHandler;
import io.github.milkdrinkers.stewards.guard.lookup.GuardLookup;
import io.github.milkdrinkers.stewards.hook.HookManager;
import io.github.milkdrinkers.stewards.listener.ListenerHandler;
import io.github.milkdrinkers.stewards.quest.BetonQuestHandler;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.steward.lookup.StewardLookup;
import io.github.milkdrinkers.stewards.threadutil.SchedulerHandler;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.TraitHandler;
import io.github.milkdrinkers.stewards.translation.TranslationHandler;
import io.github.milkdrinkers.stewards.updatechecker.UpdateHandler;
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
    private StewardsAPIProvider apiProvider;

    // Steward handlers
    private StewardTypeHandler stewardTypeHandler;
    private StewardLookup stewardLookup;
    private GuardLookup guardLookup;
    private TraitHandler traitHandler;

    private BetonQuestHandler betonQuestHandler;

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
        guardLookup = new GuardLookup(this);
        traitHandler = new TraitHandler();
        commandHandler = new CommandHandler(this);
        listenerHandler = new ListenerHandler(this);
        updateHandler = new UpdateHandler(this);
        schedulerHandler = new SchedulerHandler();
        apiProvider = new StewardsAPIProvider(this);
        betonQuestHandler = new BetonQuestHandler();

        handlers = List.of(
            configHandler,
            translationHandler,
            hookManager,
            stewardTypeHandler,
            stewardLookup,
            guardLookup,
            traitHandler,
            commandHandler,
            listenerHandler,
            updateHandler,
            schedulerHandler,
            apiProvider,
            betonQuestHandler
        );

        for (Reloadable handler : handlers)
            handler.onLoad(instance);
    }

    @Override
    public void onEnable() {
        for (Reloadable handler : handlers)
            handler.onEnable(instance);
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

    /**
     * Gets GuardLookup instance
     */
    @NotNull
    public GuardLookup getGuardLookup() {
        return guardLookup;
    }

    @SuppressWarnings("ExtractMethodRecommender")
    private void migrateOldData() {
        // Cleanup & migrate old/outdated metadata
        final IntegerDataField oldBankField = new IntegerDataField("stewards_bank_limit");
        final StringDataField architectField = new StringDataField("stewards_architect");
        final StringDataField bailiffField = new StringDataField("stewards_bailiff");
        final StringDataField portmasterField = new StringDataField("stewards_portmaster");
        final StringDataField stablemasterField = new StringDataField("stewards_stablemaster");
        final StringDataField treasurerField = new StringDataField("stewards_treasurer");
        final List<StringDataField> oldList = List.of(
            architectField,
            bailiffField,
            portmasterField,
            stablemasterField,
            treasurerField
        );
        for (Town town : TownyAPI.getInstance().getTowns()) {
            // Cleanup old bank
            if (MetaDataUtil.hasMeta(town, oldBankField)) {
                final int val = MetaDataUtil.getInt(town, oldBankField);
                town.removeMetaData(oldBankField);
                TownMetaData.setBankLimit(town, val);
            }

            // Cleanup old string fields if the data is empty or null
            for (StringDataField field : oldList) {
                if (MetaDataUtil.hasMeta(town, field)) {
                    final String data = MetaDataUtil.getString(town, field);
                    if (data == null || data.isBlank())
                        town.removeMetaData(field);
                }
            }
        }
    }
}
