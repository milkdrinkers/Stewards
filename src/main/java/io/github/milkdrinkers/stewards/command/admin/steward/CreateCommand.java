package io.github.milkdrinkers.stewards.command.admin.steward;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.command.AbstractCommand;
import io.github.milkdrinkers.stewards.command.argument.StewardTypeArgument;
import io.github.milkdrinkers.stewards.command.argument.TownArgument;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.utility.SpawnUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static io.github.milkdrinkers.stewards.command.StewardsCommand.BASE_PERM;

public final class CreateCommand extends AbstractCommand {
    public CreateCommand() {
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("create")
            .withHelp("", "")
            .withPermission(BASE_PERM + ".admin.create")
            .withArguments(
                StewardTypeArgument.argument("type"),
                TownArgument.argument("town")
            )
            .withOptionalArguments(
                new PlayerArgument("player")
            )
            .executesPlayer((sender, args) -> {
                final StewardType type = args.getByClass("type", StewardType.class);
                final Town town = args.getByClassOrDefault("town", Town.class, null);
                final Player player = args.getByClassOrDefault("player", Player.class, sender);

                if (type == null)
                    error("Steward type is not valid!");

                final StewardType architectType = StewardsAPI.getRegistry().getType(StewardTypeHandler.ARCHITECT_ID);
                final boolean isArchitect = type.equals(architectType);

                // Require town for anything but architect
                if (town == null)
                    error("You must provide a town for a steward!");

                // Prevent duplicating stewards
                if (TownMetaData.NPC.has(town, type))
                    error("The town already has a steward of this type!");

                final Location spawnLocation = SpawnUtils.getSuitableLosLocation(sender);
                if (spawnLocation == null)
                    error("The spawn location is not valid!");

                final Town townAtLoc = TownyAPI.getInstance().getTown(spawnLocation);
                if (townAtLoc == null || !townAtLoc.equals(town))
                    error("The spawn location must be inside the town!");

                final Steward steward = SpawnUtils.createSteward(type, town, player, spawnLocation);

                if (steward == null)
                    error("Failed to create steward!");

                SpawnUtils.hireSteward(steward, town, player, 0, false);
            });
    }
}
