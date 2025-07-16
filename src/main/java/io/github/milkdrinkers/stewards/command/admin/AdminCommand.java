package io.github.milkdrinkers.stewards.command.admin;

import dev.jorel.commandapi.CommandAPICommand;
import io.github.milkdrinkers.stewards.command.AbstractCommand;
import io.github.milkdrinkers.stewards.command.admin.steward.CreateArchitectCommand;
import io.github.milkdrinkers.stewards.command.admin.steward.CreateCommand;
import io.github.milkdrinkers.stewards.command.admin.steward.DeleteCommand;
import io.github.milkdrinkers.stewards.command.admin.steward.TeleportCommand;

import static io.github.milkdrinkers.stewards.command.StewardsCommand.BASE_PERM;


public final class AdminCommand extends AbstractCommand {
    public AdminCommand() {
    }

    public CommandAPICommand command() {
        return new CommandAPICommand("admin")
            .withHelp("", "")
            .withPermission(BASE_PERM + ".admin")
            .withSubcommands(
                new CreateCommand().command(),
                new DeleteCommand().command(),
                new TeleportCommand().command(),
                new CreateArchitectCommand().command()
            )
            .executes((sender, args) -> {

            });
    }
}
