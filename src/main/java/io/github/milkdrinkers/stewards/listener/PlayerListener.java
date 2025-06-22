package io.github.milkdrinkers.stewards.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Cfg;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (StewardLookup.get().isPlayerFollowed(e.getPlayer())) {
            Steward steward = StewardLookup.get().getStewardFollowingPlayer(e.getPlayer());
            if (!steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).isHired()) steward.getSettler().delete();

            StewardTrait trait = steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class);

            StewardLookup.get().removeStewardFollowingPlayer(e.getPlayer());
            steward.getSettler().getNpc().getNavigator().setTarget(trait.getAnchorLocation());
            trait.setFollowing(false);
            trait.setFollowingPlayer(null);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (StewardLookup.get().isPlayerFollowed(e.getPlayer())) {
            Steward steward = StewardLookup.get().getStewardFollowingPlayer(e.getPlayer());
            if (!steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).isHired()) {
                if (steward.getSettler().getNpc().hasTrait(ArchitectTrait.class)) {
                    steward.getSettler().getNpc().teleport(e.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    steward.stopFollowing(e.getPlayer());
                    steward.startFollowing(e.getPlayer());
                } else {
                    steward.stopFollowing(e.getPlayer());
                } 
            } else {
                steward.stopFollowing(e.getPlayer());
            }
        }
    }
}
