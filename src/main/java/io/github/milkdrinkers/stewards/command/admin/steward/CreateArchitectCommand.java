package io.github.milkdrinkers.stewards.command.admin.steward;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.command.AbstractCommand;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.utility.SpawnUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static io.github.milkdrinkers.stewards.command.StewardsCommand.BASE_PERM;

public final class CreateArchitectCommand extends AbstractCommand {
    public CreateArchitectCommand() {
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("createarchitect")
            .withHelp("", "")
            .withPermission(BASE_PERM + ".admin.createarchitect")
            .withArguments(
            )
            .withOptionalArguments(
                new PlayerArgument("player")
            )
            .executesPlayer((sender, args) -> {
                final Player player = args.getByClassOrDefault("player", Player.class, sender);

                final StewardType type = StewardsAPI.getRegistry().getType(StewardTypeHandler.ARCHITECT_ID);

                Steward steward;
                if (!player.equals(sender)) {
                    final Location spawnLocation = SpawnUtils.getSuitableLosLocation(sender);
                    if (spawnLocation == null)
                        error("The spawn location is not valid!");

                    steward = SpawnUtils.createSteward(type, null, player, spawnLocation);
                } else {
                    steward = SpawnUtils.createSteward(type, null, player, player.getLocation());
                }

                if (steward == null)
                    error("Failed to create steward!");
            });
    }
}
