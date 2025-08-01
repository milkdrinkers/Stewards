package io.github.milkdrinkers.stewards.steward.lookup;

import io.github.milkdrinkers.settlers.api.settler.AbstractSettler;
import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.steward.Steward;
import net.citizensnpcs.api.npc.NPC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

/**
 * A lookup table for all Steward related things. Contains sub-lookup tables:</br>
 * - {@link StewardTownLookup} at {@link #town()}</br>
 * - {@link StewardFollowLookup} at {@link #follow()}</br>
 * - {@link ArchitectLookup} at {@link #architect()}</br>
 * </br>
 * Itself stores all loaded stewards mapped to their citizen npc uuid.
 */
public class StewardLookup implements Reloadable {
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Stewards plugin;
    private final HashMap<UUID, Steward> settlerStewardHashmap = new HashMap<>(); // NPC UUID to Steward
    private final StewardTownLookup townLookup = new StewardTownLookup(this);
    private final StewardFollowLookup followLookup = new StewardFollowLookup(this);
    private final ArchitectLookup architectLookup = new ArchitectLookup(this);

    /**
     * Get steward lookup table statically.
     *
     * @return the steward lookup table
     * @deprecated This method is an abomination an will be phased out in time
     */
    @Deprecated
    public static StewardLookup get() {
        return Stewards.getInstance().getStewardLookup();
    }

    /**
     * Instantiates a new Steward lookup table.
     *
     * @param plugin the plugin
     */
    public StewardLookup(Stewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(Stewards plugin) {
        townLookup.onLoad(plugin);
        followLookup.onLoad(plugin);
        architectLookup.onLoad(plugin);
    }

    @Override
    public void onEnable(Stewards plugin) {
        townLookup.onEnable(plugin);
        followLookup.onEnable(plugin);
        architectLookup.onEnable(plugin);
    }

    @Override
    public void onDisable(Stewards plugin) {
        townLookup.onDisable(plugin);
        followLookup.onDisable(plugin);
        architectLookup.onDisable(plugin);
    }

    /**
     * Get the steward town lookup table.
     *
     * @return the lookup table
     */
    public @NotNull StewardTownLookup town() {
        return townLookup;
    }

    /**
     * Get the following lookup table.
     *
     * @return the lookup table
     */
    public @NotNull StewardFollowLookup follow() {
        return followLookup;
    }

    /**
     * Get the architect lookup table.
     *
     * @return the lookup table
     */
    public @NotNull ArchitectLookup architect() {
        return architectLookup;
    }

    /**
     * Gets a steward from it's UUID.
     *
     * @param uuid the steward uuid
     * @return the steward or null
     */
    public @Nullable Steward get(UUID uuid) {
        return settlerStewardHashmap.get(uuid);
    }

    /**
     * Gets a steward from the NPC object.
     *
     * @param npc the npc
     * @return the steward or null
     * @see #get(UUID)
     */
    public @Nullable Steward get(NPC npc) {
        return get(npc.getUniqueId());
    }

    /**
     * Gets a steward from the AbstractSettler object.
     *
     * @param settler the settler
     * @return the steward or null
     * @see #get(UUID)
     */
    public @Nullable Steward get(AbstractSettler settler) {
        return get(settler.getNpc());
    }

    /**
     * Adds a steward to the lookup table.
     *
     * @param steward the steward
     */
    public void add(Steward steward) {
        settlerStewardHashmap.put(steward.getUniqueId(), steward);
    }

    /**
     * Removes a steward from the lookup table by its UUID.
     *
     * @param uuid the uuid
     */
    public void remove(UUID uuid) {
        settlerStewardHashmap.remove(uuid);
    }

    /**
     * Removes a steward from the lookup table by its NPC object.
     *
     * @param npc the npc
     * @see #remove(UUID)
     */
    public void remove(NPC npc) {
        remove(npc.getUniqueId());
    }

    /**
     * Removes a steward from the lookup table by its AbstractSettler object.
     *
     * @param settler the settler
     * @see #remove(UUID)
     */
    public void remove(AbstractSettler settler) {
        remove(settler.getNpc());
    }

    /**
     * Removes a steward from the lookup table.
     *
     * @param steward the steward
     * @see #remove(UUID)
     */
    public void remove(Steward steward) {
        remove(steward.getSettler());
    }

    public void clear() {
        settlerStewardHashmap.clear();
    }
}
