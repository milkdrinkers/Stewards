package io.github.milkdrinkers.stewards.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.settlers.api.settler.SettlerBuilder;
import io.github.milkdrinkers.settlers.api.settler.Townfolk;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.trait.traits.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.traits.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Appearance;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.stewards.utility.Logger;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;

/**
 * Class containing the code for the example command.
 */
class StewardsCommand {
    private static final String BASE_PERM = "stewards.command";

    /**
     * Instantiates and registers a new command.
     */
    protected StewardsCommand() {
        new CommandAPICommand("stewards")
            .withSubcommands(
                new TranslationCommand().command(),
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

        try {
            boolean female = Math.random() > 0.5;
            String name = Appearance.getMaleName();
            if (female)
                name = Appearance.getFemaleName();

            Location spawnLocation = player.getLocation().add(1, 0, 0);

            Townfolk settler = new SettlerBuilder()
                .setName(name)
                .setLocation(spawnLocation)
                .createTownfolk();

            Steward steward = Steward.builder()
                .setStewardType(Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(
                    Stewards.getInstance().getStewardTypeHandler().ARCHITECT_ID))
                .setDailyUpkeepCost(0)
                .setIsEnabled(true)
                .setIsHidden(false)
                .setLevel(1)
                .setSettler(settler)
                .build();

            StewardTrait stewardTrait = steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class);
            stewardTrait.setFemale(female);
            stewardTrait.setLevel(1);
            stewardTrait.setFollowingPlayer(player);

            ArchitectTrait architectTrait = steward.getSettler().getNpc().getOrAddTrait(ArchitectTrait.class);
            architectTrait.setCreateTime(Instant.now());
            architectTrait.setSpawningPlayer(player.getUniqueId());

            HologramTrait hologramTrait = steward.getSettler().getNpc().getOrAddTrait(HologramTrait.class);
            hologramTrait.addLine("&7[&6" + steward.getStewardType().getName() + "&7]");

            steward.getSettler().getNpc().addTrait(LookClose.class);
            steward.getSettler().getNpc().getOrAddTrait(LookClose.class).setRange(16);

            if (female) {
                Appearance.applyFemaleStewardSkin(steward);
            } else {
                Appearance.applyMaleStewardSkin(steward);
            }

            StewardLookup.get().registerSteward(steward);
            StewardLookup.get().setArchitect(player, steward);

            settler.spawn();
        } catch (InvalidStewardException e) {
            throw new RuntimeException(e);
        }
    }
}
