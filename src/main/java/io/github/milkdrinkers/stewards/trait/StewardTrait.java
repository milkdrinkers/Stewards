package io.github.milkdrinkers.stewards.trait;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.alathra.alathraports.core.carriagestations.CarriageStation;
import io.github.alathra.alathraports.core.ports.Port;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.settlers.api.enums.ClickType;
import io.github.milkdrinkers.settlers.api.event.settler.lifetime.interact.SettlerClickedEvent;
import io.github.milkdrinkers.settlers.api.event.settler.lifetime.spawning.SettlerSpawnEvent;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.gui.StewardBaseGui;
import io.github.milkdrinkers.stewards.gui.StewardUserGui;
import io.github.milkdrinkers.stewards.hook.Hook;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.utility.CheckUtils;
import io.github.milkdrinkers.stewards.utility.Logger;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.npc.NPC;
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
        if (StewardsAPI.getLookup().get(this.getNPC()).getStewardType().maxLevel() == this.level)
            return false;
        level++;
        return true;
    }

    public void load(DataKey key) {
    }

    public void save(DataKey key) {
    }

    @EventHandler
    public void onSpawn(SettlerSpawnEvent e) {
        final NPC npc = e.getSettler().getNpc();
        if (npc != this.getNPC())
            return;

        if (!npc.hasTrait(StewardTrait.class))
            return;

        if (anchorLocation == null && !npc.hasTrait(ArchitectTrait.class))
            this.anchorLocation = npc.getEntity().getLocation();
    }

    @EventHandler
    public void click(SettlerClickedEvent e) {
        final NPC npc = e.getSettler().getNpc();
        if (npc != this.getNPC())
            return;

        if (e.getClickType().equals(ClickType.SHIFT_LEFT) || e.getClickType().equals(ClickType.SHIFT_RIGHT))
            return;

        if (!npc.hasTrait(StewardTrait.class))
            return;

        final Resident resident = TownyAPI.getInstance().getResident(e.getClicker());
        if (resident == null)
            return;

        final boolean isMayor = CheckUtils.isMayor(e.getClicker());
        final boolean isAdmin = CheckUtils.isAdmin(e.getClicker());

        final Steward steward = StewardsAPI.getLookup().get(e.getSettler());
        if (steward == null)
            return;

        // Allow menu for steward
        if (steward.isFounder() && steward.getNpc().getOrAddTrait(ArchitectTrait.class).getSpawningPlayer().equals(e.getClicker().getUniqueId())) {
            StewardBaseGui.createBaseGui(steward, e.getClicker()).open(e.getClicker());
            return;
        }

        final Town stewardTown = TownyAPI.getInstance().getTown(steward.getTownUUID());

        if (stewardTown == null) {
            e.getClicker().sendMessage(ColorParser.of("<red>This steward must be part of a town to be interacted with.").build()); // TODO Translate
            return;
        }

        if (!isAdmin && (!isMayor || !CheckUtils.isSameTown(e.getClicker(), steward)) && steward.getTrait().isHired()) {
            if (steward.hasTrait(PortmasterTrait.class)) {
                final Port port = PortsAPI.getPortFromTown(stewardTown);
                if (port != null) {
                    PortsAPI.openTravelMenu(e.getClicker(), port);
                    return;
                }
            } else if (steward.hasTrait(StablemasterTrait.class)) {
                final CarriageStation station = PortsAPI.getCarriageStationFromTown(stewardTown);
                if (station != null) {
                    PortsAPI.openTravelMenu(e.getClicker(), station);
                    return;
                }
            }
        }

        final Town clickerTown = resident.getTownOrNull();

        if (!isAdmin && clickerTown == null) {
            e.getClicker().sendMessage(ColorParser.of("<red>You must be part of a town to interact with stewards.").build()); // TODO Translate
            return;
        }

        if (!isAdmin && !CheckUtils.isSameTown(stewardTown, clickerTown)) {
            e.getClicker().sendMessage(ColorParser.of("<red>You must be part of a town to interact with stewards.").build()); // TODO Translate
            return;
        }

        if (!isAdmin && !isMayor) {
            // Open user architect menu for non-mayors
            if (steward.getStewardType() == Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(StewardTypeHandler.ARCHITECT_ID) && isHired()) {
                StewardUserGui.createBaseGui(steward, e.getClicker()).open(e.getClicker());
                return;
            }

            e.getClicker().sendMessage(ColorParser.of("<red>You must be mayor or co-mayor to interact with stewards.").build()); // TODO Translate
            return;
        }

        if (following && e.getClicker() != this.getFollowingPlayer()) return;

        if (!following && StewardsAPI.getLookupFollow().isFollowed(e.getClicker())) return;

        if (striking) {
            e.getClicker().sendMessage(ColorParser.of("<red>This steward is currently striking. Talk to your architect to get them back.").build());
            return;
        }

        StewardBaseGui.createBaseGui(steward, e.getClicker()).open(e.getClicker());
    }

    @EventHandler
    public void onNavigationCancelled(NavigationCancelEvent e) {
        if (e.getNPC() != this.getNPC()) return;

        if (!npc.hasTrait(StewardTrait.class))
            return;

        if (e.getCancelReason() == CancelReason.STUCK) {
            Steward steward = StewardsAPI.getLookup().get((e.getNPC()));
            if (steward == null)
                return;

            Player player = followingPlayer;
            steward.stopFollowing(player);
            steward.startFollowing(player);
        }
    }

    @Override
    public void run() {
        if (!following) return;
        if (npc.getEntity().getLocation() == anchorLocation) return;
        if (npc.hasTrait(ArchitectTrait.class) && !hired) return;

        // TODO Perf, this doesn't need to run every tick
        if (TownyAPI.getInstance().getTown(npc.getEntity().getLocation()) == null
            || !TownyAPI.getInstance().getTown(npc.getEntity().getLocation()).getUUID().equals(townUUID)) {
            followingPlayer.sendMessage(ColorParser.of("<red>Stewards aren't allowed to move outside of their town.").build()); // TODO Translation
            StewardsAPI.getLookup().get(npc).stopFollowing(followingPlayer);
        }
    }

}
