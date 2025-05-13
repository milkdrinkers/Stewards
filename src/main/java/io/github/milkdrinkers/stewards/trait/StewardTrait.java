package io.github.milkdrinkers.stewards.trait;

import com.palmergames.bukkit.towny.object.TownBlock;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.stewards.gui.StewardBaseGui;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

public class StewardTrait extends Trait {

    protected StewardTrait() {
        super("steward");
    }

    boolean following = false;
    Player followingPlayer;
    @Persist
    boolean female; // Stored to keep track of whether the skin and name is "male" or "female"
    @Persist
    Location anchorLocation;
    @Persist
    int level;
    @Persist
    boolean hired = false;
    @Persist
    TownBlock townBlock;
    @Persist
    UUID townUUID;
    @Persist
    boolean striking = false;

    public TownBlock getTownBlock() {
        return townBlock;
    }

    public void setTownBlock(TownBlock townBlock) {
        this.townBlock = townBlock;
    }

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Player getFollowingPlayer() {
        return followingPlayer;
    }

    public void setFollowingPlayer(Player player) {
        this.followingPlayer = player;
    }

    public boolean isHired() {
        return hired;
    }

    public void setHired(boolean hired) {
        this.hired = hired;
    }

    public void hire() {
        this.hired = true;
    }

    public boolean isStriking() {
        return striking;
    }

    public void setStriking(boolean striking) {
        this.striking = striking;
    }

    /**
     * Attempts to level up the steward.
     *
     * @return false if max level is reached and true if steward leveled up.
     */
    public boolean levelUp() {
        if (StewardLookup.get().getSteward(this.getNPC()).getStewardType().getMaxLevel() == this.level)
            return false;
        level++;
        return true;
    }

    public void load(DataKey key) {
        female = key.getBoolean("female");
        hired = key.getBoolean("hired");
        striking = key.getBoolean("striking");

        level = key.getInt("level");

        anchorLocation = (Location) key.getRaw("anchorlocation");
        townUUID = (UUID) key.getRaw("townuuid"); // TODO don't respawn if this doesn't exist
        townBlock = (TownBlock) key.getRaw("townblock");
    }

    public void save(DataKey key) {
        key.setBoolean("female", female);
        key.setBoolean("hired", hired);
        key.setBoolean("striking", striking);

        key.setInt("level", level);

        key.setRaw("anchorlocation", anchorLocation);
        key.setRaw("townuuid", townUUID);
        key.setRaw("townblock", townBlock);
    }

    @EventHandler
    public void click(NPCRightClickEvent e) {
        if (e.getNPC() != this.getNPC()) return;

        if (e.getClicker().isSneaking()) return;

        if (following && e.getClicker() != this.getFollowingPlayer()) return;

        if (!following && StewardLookup.get().isPlayerFollowed(e.getClicker())) return;

        if (striking) {
            e.getClicker().sendMessage(ColorParser.of("<red>This steward is currently striking. Talk to your architect to get them back.").build());
            return;
        }

        Steward steward = StewardLookup.get().getSteward((e.getNPC()));
        if (steward == null) return;

        StewardBaseGui.createBaseGui(steward, e.getClicker()).open(e.getClicker());
    }

//    @EventHandler
//    public void onMove(NPCMoveEvent e) {
//        if (e.getNPC() != this.getNPC()) return;
//        if (e.getNPC().hasTrait(ArchitectTrait.class) && !hired) return;
//
//        if (TownyAPI.getInstance().getTown(e.getTo()) == null) {
//            e.setCancelled(true);
//
//            StewardLookup.get().removeStewardFollowingPlayer(followingPlayer);
//            followingPlayer.sendMessage("<red>Stewards aren't allowed to move outside of their town.");
//
//            following = false;
//            followingPlayer = null;
//            npc.getNavigator().setTarget(anchorLocation);
//            return;
//        }
//
//        if (!TownyAPI.getInstance().getTown(e.getTo()).getUUID().equals(townUUID)) {
//            e.setCancelled(true);
//
//            StewardLookup.get().removeStewardFollowingPlayer(followingPlayer);
//            followingPlayer.sendMessage("<red>Stewards aren't allowed to move outside of their town.");
//
//            following = false;
//            followingPlayer = null;
//            npc.getNavigator().setTarget(anchorLocation);
//        }
//    }

//    @Override
//    public void run() {
//        if (!following) return;
//        if (npc.getEntity().getLocation() == anchorLocation) return;
//        if (npc.hasTrait(ArchitectTrait.class) && !hired) return;
//
//        if (TownyAPI.getInstance().getTown(npc.getEntity().getLocation()) == null
//            || !TownyAPI.getInstance().getTown(npc.getEntity().getLocation()).getUUID().equals(townUUID) ) {
//
//            StewardLookup.get().removeStewardFollowingPlayer(followingPlayer);
//            followingPlayer.sendMessage("<red>Stewards aren't allowed to move outside of their town.");
//
//            following = false;
//            followingPlayer = null;
//            npc.getNavigator().setTarget(anchorLocation);
//            return;
//        }
//    }

}
