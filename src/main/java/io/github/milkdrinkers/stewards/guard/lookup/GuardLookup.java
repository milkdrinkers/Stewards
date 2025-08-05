package io.github.milkdrinkers.stewards.guard.lookup;

import io.github.milkdrinkers.settlers.api.settler.AbstractSettler;
import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.guard.Guard;
import net.citizensnpcs.api.npc.NPC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

/**
 * A lookup table for all Guard related things. Contains sub-lookup tables:</br>
 * - {@link GuardTownLookup} at {@link #town()}</br>
 * - {@link GuardFollowLookup} at {@link #follow()}</br>
 * </br>
 * Itself stores all loaded guards mapped to their citizen npc uuid.
 */
public class GuardLookup implements Reloadable {
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Stewards plugin;
    private final HashMap<UUID, Guard> settlerGuardHashMap = new HashMap<>(); // NPC UUID to Steward
    private final GuardTownLookup townLookup = new GuardTownLookup(this);
    private final GuardFollowLookup followLookup = new GuardFollowLookup(this);

    /**
     * Instantiates a new guard lookup table.
     *
     * @param plugin the plugin
     */
    public GuardLookup(Stewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(Stewards plugin) {
        townLookup.onLoad(plugin);
        followLookup.onLoad(plugin);
    }

    @Override
    public void onEnable(Stewards plugin) {
        townLookup.onEnable(plugin);
        followLookup.onEnable(plugin);
    }

    @Override
    public void onDisable(Stewards plugin) {
        townLookup.onDisable(plugin);
        followLookup.onDisable(plugin);
    }

    /**
     * Get the guard town lookup table.
     *
     * @return the lookup table
     */
    public @NotNull GuardTownLookup town() {
        return townLookup;
    }

    /**
     * Get the guard following lookup table.
     *
     * @return the lookup table
     */
    public @NotNull GuardFollowLookup follow() {
        return followLookup;
    }

    /**
     * Gets a guard from it's UUID.
     *
     * @param uuid the steward uuid
     * @return the guard or null
     */
    public @Nullable Guard get(UUID uuid) {
        return settlerGuardHashMap.get(uuid);
    }

    /**
     * Gets a guard from the NPC object.
     *
     * @param npc the npc
     * @return the guard or null
     * @see #get(UUID)
     */
    public @Nullable Guard get(NPC npc) {
        return get(npc.getUniqueId());
    }

    /**
     * Gets a guard from the AbstractSettler object.
     *
     * @param settler the settler
     * @return the guard or null
     * @see #get(UUID)
     */
    public @Nullable Guard get(AbstractSettler settler) {
        return get(settler.getNpc());
    }

    /**
     * Adds a guard to the lookup table.
     *
     * @param guard the guard
     */
    public void add(Guard guard) {
        settlerGuardHashMap.put(guard.getUniqueId(), guard);
    }

    /**
     * Removes a guard from the lookup table by its UUID.
     *
     * @param uuid the uuid
     */
    public void remove(UUID uuid) {
        settlerGuardHashMap.remove(uuid);
    }

    /**
     * Removes a guard from the lookup table by its NPC object.
     *
     * @param npc the npc
     * @see #remove(UUID)
     */
    public void remove(NPC npc) {
        remove(npc.getUniqueId());
    }

    /**
     * Removes a guard from the lookup table by its AbstractSettler object.
     *
     * @param settler the settler
     * @see #remove(UUID)
     */
    public void remove(AbstractSettler settler) {
        remove(settler.getNpc());
    }

    /**
     * Removes a guard from the lookup table.
     *
     * @param guard the guard
     * @see #remove(UUID)
     */
    public void remove(Guard guard) {
        remove(guard.getSettler());
    }

    public void clear() {
        settlerGuardHashMap.clear();
    }
}
