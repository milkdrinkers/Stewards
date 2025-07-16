package io.github.milkdrinkers.stewards.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.command.admin.AdminCommand;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.stewards.utility.Logger;
import io.github.milkdrinkers.stewards.utility.SpawnUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static io.github.milkdrinkers.stewards.steward.StewardTypeHandler.ARCHITECT_ID;

/**
 * Class containing the code for the example command.
 */
public class StewardsCommand {
    public static final String BASE_PERM = "stewards.command";

    /**
     * Instantiates and registers a new command.
     */
    protected StewardsCommand() {
        new CommandAPICommand("stewards")
            .withSubcommands(
                new TranslationCommand().command(),
                new AdminCommand().command(),
                new CommandAPICommand("getarchitect")
                    .withArguments(new PlayerArgument("player"))
                    .withPermission(BASE_PERM + ".getarchitect")
                    .executes(this::executorSpawn)
            )
            .register();
    }

    private void executorSpawn(CommandSender sender, CommandArguments args) {
        Player player = (Player) args.get("player");

        if (player == null) {
            Logger.get().error("Player argument is null.");
            return;
        }

        if (!Cfg.get().getStringList("architect.allowed-worlds").contains(player.getWorld().getName())) {
            Logger.get().error("Player is not in an allowed world.");
            return;
        }

        final StewardType type = Objects.requireNonNull(StewardsAPI.getRegistry().getType(ARCHITECT_ID), "Architect type not found in registry");
        SpawnUtils.createSteward(type, null, player, null);
    }
}
