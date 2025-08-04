package io.github.milkdrinkers.stewards.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.lookup.StewardLookup;
import io.github.milkdrinkers.stewards.trait.traits.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.traits.StewardTrait;
import io.github.milkdrinkers.stewards.utility.DeleteUtils;
import io.github.milkdrinkers.threadutil.Scheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("unused")
public class PlayerListener implements Listener {
    private final Stewards plugin;
    private final @NotNull StewardLookup lookup;

    public PlayerListener(Stewards plugin) {
        this.plugin = plugin;
        this.lookup = plugin.getStewardLookup();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        final Optional<Steward> opt = lookup.follow().getFollower(e.getPlayer());
        if (opt.isPresent()) {
            final Steward steward = opt.get();

            // Founders have their own separate lifecycle logic
            if (steward.isFounder())
                return;

            if (!steward.getTrait().isHired()) {
                DeleteUtils.dismiss(
                    steward,
                    steward.getTownUUID() != null ? TownyAPI.getInstance().getTown(steward.getTownUUID()) : null,
                    e.getPlayer(),
                    false
                );
                return;
            }

            final StewardTrait trait = steward.getTrait();
            steward.stopFollowing(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        final Optional<Steward> opt = lookup.follow().getFollower(e.getPlayer());
        if (opt.isPresent()) {
            final Steward steward = opt.get();
            // If unhired architect
            if (
                !steward.getTrait().isHired() &&
                    steward.getSettler().getNpc().hasTrait(ArchitectTrait.class)
            ) {
                // Teleport along with player
                Scheduler.sync(() -> {
                        steward.stopFollowing(e.getPlayer());
                        steward.getSettler().despawn();
                    }).delay(5)
                    .sync(() -> steward.getSettler().spawn(e.getTo()))
                    .delay(5)
                    .sync(() -> steward.startFollowing(e.getPlayer()))
                    .execute();
            } else {
                steward.stopFollowing(e.getPlayer());
            }
        }
    }
}
