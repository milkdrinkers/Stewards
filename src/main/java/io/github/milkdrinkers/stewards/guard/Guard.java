package io.github.milkdrinkers.stewards.guard;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import io.github.milkdrinkers.settlers.api.settler.AbstractSettler;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import io.github.milkdrinkers.stewards.trait.traits.guard.GuardTrait;
import io.github.milkdrinkers.stewards.trait.traits.steward.StewardTrait;
import net.citizensnpcs.api.ai.TeleportStuckAction;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class Guard {
    private final AbstractSettler settler;
    private final boolean isEnabled;
    private final boolean isHidden;
    private final double dailyUpkeepCost;

    public Guard(AbstractSettler settler, @Nullable UUID townUUID, boolean isEnabled, boolean isHidden, double dailyUpkeepCost) {
        this.settler = settler;
        this.isEnabled = isEnabled;
        this.isHidden = isHidden;
        this.dailyUpkeepCost = dailyUpkeepCost;
        this.settler.getNpc().getOrAddTrait(StewardTrait.class);

        // Only set if overrides provided by builder (otherwise we'd override trait values with null)
        if (townUUID != null)
            setTownUUID(townUUID);
    }

    /**
     * Makes the guard start following a player.
     *
     * @param player the player
     */
    public void startFollowing(Player player) {
        // Cancel old follow objective
        if (getTrait().isFollowing())
            StewardsAPI.getGuardLookupFollow().getFollowee(this).ifPresent(this::stopFollowing);

        StewardsAPI.getGuardLookupFollow().add(player, this);
        getNpc().getNavigator().setTarget(player, false);
        getNpc().getNavigator().getLocalParameters().stuckAction(TeleportStuckAction.INSTANCE);
        getNpc().getNavigator().getLocalParameters().speedModifier(1.5f);
        getNpc().getNavigator().getLocalParameters().distanceMargin(4.0);
        getTrait().setFollowing(true);
        getTrait().setFollowingPlayer(player);
    }

    /**
     * Make the guard stop following a player.
     *
     * @param player the player
     */
    public void stopFollowing(Player player) {
        stopFollowing(player, false);
    }

    /**
     * Make the guard stop following a player and set an anchorpoint at the settlers current location.
     *
     * @param player            the player
     * @param setAnchorLocation whether to set the anchor location
     */
    public void stopFollowing(Player player, boolean setAnchorLocation) {
        getNpc().getNavigator().cancelNavigation();
        getTrait().setFollowing(false);
        getTrait().setFollowingPlayer(null);
        if (setAnchorLocation && getSettler().isSpawned() && getSettler().getEntity() != null)
            getTrait().setAnchorLocation(getSettler().getEntity().getLocation());
        getNpc().getNavigator().setTarget(getTrait().getAnchorLocation());
        getNpc().getNavigator().getLocalParameters().stuckAction(TeleportStuckAction.INSTANCE);
        getNpc().getNavigator().getLocalParameters().speedModifier(1.5f);
        getNpc().getNavigator().getLocalParameters().distanceMargin(0.0);
        StewardsAPI.getLookupFollow().remove(player);
    }

    public void walkToAnchor(Location location) {
        getNpc().getNavigator().cancelNavigation();
        getTrait().setFollowing(false);
        getTrait().setFollowingPlayer(null);
        getTrait().setAnchorLocation(location);
        getNpc().getNavigator().setTarget(getTrait().getAnchorLocation());
        getNpc().getNavigator().getLocalParameters().stuckAction(TeleportStuckAction.INSTANCE);
        getNpc().getNavigator().getLocalParameters().speedModifier(1.5f);
        getNpc().getNavigator().getLocalParameters().distanceMargin(0.0);
    }

    public AbstractSettler getSettler() {
        return settler;
    }

    /**
     * Gets the town UUID of this guard.
     *
     * @return town uuid
     */
    public UUID getTownUUID() {
        return getTrait().getTownUUID();
    }

    /**
     * Sets the town UUID of this guard.
     *
     * @param townUUID town uuid
     */
    public void setTownUUID(UUID townUUID) {
        getTrait().setTownUUID(townUUID);
    }

    /**
     * Returns the town block this guard is standing in
     *
     * @return the town block or null
     */
    public @Nullable TownBlock getTownBlock() {
        return TownyAPI.getInstance().getTownBlock(getSettler().getNpc().getStoredLocation());
    }

    /**
     * Returns the Steward UUID (Grabbed from the settler and then the Citizens NPC UUID)
     *
     * @return uuid
     */
    public @NotNull UUID getUniqueId() {
        return getSettler().getNpc().getUniqueId();
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public double getDailyUpkeepCost() {
        return dailyUpkeepCost;
    }

    /**
     * Gets the NPC for this guard of the settler.
     *
     * @return citizens npc
     */
    public NPC getNpc() {
        return getSettler().getNpc();
    }

    /**
     * Shorthand for {@link NPC#hasTrait(Class)}.
     *
     * @param trait the trait
     * @return true if has, false otherwise
     */
    public boolean hasTrait(Class<? extends Trait> trait) {
        return getNpc().hasTrait(trait);
    }

    /**
     * Get the guard trait of this guard.
     *
     * @return the trait
     */
    public @NotNull GuardTrait getTrait() {
        return getSettler().getNpc().getOrAddTrait(GuardTrait.class);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Guard steward)) return false;
        return Objects.equals(getUniqueId(), steward.getUniqueId());
    }

    @Override
    public int hashCode() {
        return getTownUUID().hashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AbstractSettler settler;
        private UUID townUUID;
        private boolean isEnabled;
        private boolean isHidden;
        private double dailyUpkeepCost;

        public Builder setSettler(@NotNull AbstractSettler settler) {
            this.settler = settler;
            return this;
        }

        public Builder setTownUUID(@NotNull UUID townUUID) {
            this.townUUID = townUUID;
            return this;
        }

        public Builder setIsEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public Builder setIsHidden(boolean isHidden) {
            this.isHidden = isHidden;
            return this;
        }

        public Builder setDailyUpkeepCost(double dailyUpkeepCost) {
            this.dailyUpkeepCost = dailyUpkeepCost;
            return this;
        }

        public Guard build() throws InvalidStewardException {
            if (settler == null)
                throw new InvalidStewardException("Settler is null");

            return new Guard(settler, townUUID, isEnabled, isHidden, dailyUpkeepCost);
        }
    }
}
