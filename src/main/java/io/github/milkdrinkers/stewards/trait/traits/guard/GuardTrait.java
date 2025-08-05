package io.github.milkdrinkers.stewards.trait.traits.guard;

import io.github.milkdrinkers.stewards.api.StewardsAPI;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

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

}
