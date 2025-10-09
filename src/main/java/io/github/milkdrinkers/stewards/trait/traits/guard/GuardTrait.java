package io.github.milkdrinkers.stewards.trait.traits.guard;

import com.palmergames.bukkit.towny.TownyAPI;
import io.github.milkdrinkers.settlers.api.enums.ClickType;
import io.github.milkdrinkers.settlers.api.event.settler.lifetime.interact.SettlerClickedEvent;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.gui.guard.GuardGui;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.utility.Cfg;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.mcmonkey.sentinel.SentinelTrait;

import java.util.UUID;

import static io.github.milkdrinkers.stewards.steward.StewardTypeHandler.GUARDCAPTAIN_ID;

public class GuardTrait extends Trait {
    protected GuardTrait() {
        super("stewardsguard");
    }

    boolean following = false;
    Player followingPlayer;
    @Persist
    Location anchorLocation;
    @Persist
    boolean female; // Stored to keep track of whether the skin and name is "male" or "female"
    @Persist("townuuid")
    UUID townUUID;
    @Persist
    boolean striking = false;
    @Persist
    int meleeRange = 10;
    @Persist
    int rangedRange = 20;
    @Persist
    int chaseRange = 20;
    @Persist
    int wanderRange = 0;
    @Persist
    boolean ranged = false;
    SentinelTrait sentinelTrait;

    public UUID getTownUUID() {
        return townUUID;
    }

    public void setTownUUID(UUID townUUID) {
        this.townUUID = townUUID;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public Location getAnchorLocation() {
        return anchorLocation;
    }

    public void setAnchorLocation(Location anchorLocation) {
        this.anchorLocation = anchorLocation;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public Player getFollowingPlayer() {
        return followingPlayer;
    }

    public void setFollowingPlayer(Player player) {
        this.followingPlayer = player;
    }

    public boolean isStriking() {
        return striking;
    }

    public void setStriking(boolean striking) {
        this.striking = striking;
    }

    public int getMeleeRange() {
        return meleeRange;
    }

    public void setMeleeRange(int meleeRange) {
        this.meleeRange = meleeRange;
    }

    public int getRangedRange() {
        return rangedRange;
    }

    public void setRangedRange(int rangedRange) {
        this.rangedRange = rangedRange;
    }

    public int getChaseRange() {
        return chaseRange;
    }

    public void setChaseRange(int chaseRange) {
        this.chaseRange = chaseRange;
    }

    public int getWanderRange() {
        return wanderRange;
    }

    public void setWanderRange(int wanderRange) {
        this.wanderRange = wanderRange;
    }

    public boolean isRanged() {
        return ranged;
    }

    public void setRanged(boolean ranged) {
        this.ranged = ranged;
        if (ranged) {
            sentinelTrait.range = rangedRange;
        } else {
            sentinelTrait.range = meleeRange;
        }
    }

    public boolean increaseMeleeRange() {
        if (meleeRange >= Cfg.get().getInt("guard.melee-range.max") )
            return false;
        meleeRange++;

        if (!ranged)
            sentinelTrait.range = meleeRange;
        return true;
    }
    
    public boolean decreaseMeleeRange() {
        if (meleeRange <= Cfg.get().getInt("guard.melee-range.min") )
            return false;
        meleeRange--;

        if (!ranged)
            sentinelTrait.range = meleeRange;
        return true;
    }

    public boolean increaseRangedRange() {
        if (rangedRange >= Cfg.get().getInt("guard.ranged-range.max"))
            return false;
        rangedRange++;

        if (ranged)
            sentinelTrait.range = rangedRange;
        return true;
    }

    public boolean decreaseRangedRange() {
        if (rangedRange <= Cfg.get().getInt("guard.ranged-range.min"))
            return false;
        rangedRange--;

        if (ranged)
            sentinelTrait.range = rangedRange;
        return true;
    }

    public boolean increaseChaseRange() {
        if (chaseRange >= Cfg.get().getInt("guard.chase-range.max"))
            return false;
        chaseRange++;

        sentinelTrait.chaseRange = chaseRange;
        return true;
    }

    public boolean decreaseChaseRange() {
        if (chaseRange <= Cfg.get().getInt("guard.chase-range.min"))
            return false;
        chaseRange--;

        sentinelTrait.chaseRange = chaseRange;
        return true;
    }

    public boolean increaseWanderRange() {
        if (wanderRange >= Cfg.get().getInt("guardcaptain.wander-range.level-" +
            TownMetaData.NPC.get(
                TownyAPI.getInstance().getTown(townUUID),
                StewardsAPI.getRegistry().getType(GUARDCAPTAIN_ID)
            )))
            return false;
        wanderRange++;
        return true;
    }

    public boolean decreaseWanderRange() {
        if (wanderRange < 0)
            return false;
        wanderRange--;
        return true;
    }

    @EventHandler
    public void click(SettlerClickedEvent e) {
        if (e.getSettler().getNpc() != this.getNPC())
            return;

        if (e.getClickType().equals(ClickType.SHIFT_RIGHT)) {
            GuardGui.createGui(StewardsAPI.getGuardLookup().get(this.getNPC()), e.getClicker()).open(e.getClicker());
        }
    }

    @Override
    public void onAttach() {
        sentinelTrait = this.getNPC().getOrAddTrait(SentinelTrait.class);
        if (ranged) {
            sentinelTrait.range = rangedRange;
        } else {
            sentinelTrait.range = meleeRange;
        }

        sentinelTrait.chaseRange = chaseRange;

        sentinelTrait.rangedChase = true;
    }
}
