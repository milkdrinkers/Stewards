package io.github.milkdrinkers.stewards.command.admin.steward;

import com.palmergames.bukkit.towny.object.Town;
import dev.jorel.commandapi.CommandAPICommand;
import io.github.milkdrinkers.stewards.command.AbstractCommand;
import io.github.milkdrinkers.stewards.command.argument.StewardTypeArgument;
import io.github.milkdrinkers.stewards.command.argument.TownArgument;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import org.bukkit.Location;

import static io.github.milkdrinkers.stewards.command.StewardsCommand.BASE_PERM;

public final class TeleportCommand extends AbstractCommand {
    public TeleportCommand() {
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("tp")
            .withHelp("", "")
            .withPermission(BASE_PERM + ".admin.teleport")
            .withArguments(
                StewardTypeArgument.argument("type"),
                TownArgument.argument("town")
            )
            .executesPlayer((sender, args) -> {
                final StewardType type = args.getByClass("type", StewardType.class);
                final Town town = args.getByClass("town", Town.class);

                if (type == null)
                    error("Steward type is not valid!");

                if (town == null)
                    error("Town is not valid!");

                if (!TownMetaData.NPC.has(town, type))
                    error("Town does not have steward of that type!");

                final Steward steward = TownMetaData.NPC.getSteward(town, type);
                if (steward == null)
                    error("Failed to find steward!");

                final Location loc = steward.getNpc().getStoredLocation();
                sender.teleportAsync(loc);
            });
    }
}
