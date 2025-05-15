package io.github.milkdrinkers.stewards.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.milkdrinkers.settlers.api.settler.SettlerBuilder;
import io.github.milkdrinkers.settlers.api.settler.Townfolk;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.trait.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Appearance;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class containing the code for the example command.
 */
class StewardsCommand {
    private static final String BASE_PERM = "example.command";

    /**
     * Instantiates and registers a new command.
     */
    protected StewardsCommand() {
        new CommandAPICommand("stewards")
            .withSubcommands(
                new TranslationCommand().command()
            )
            .executes(this::executorSpawn)
            .register();
    }

    private void executorSpawn(CommandSender sender, CommandArguments args) {
        try {
            boolean female = Math.random() > 0.5; // TODO most up to date spawn logic.
            String name = Appearance.getMaleName();
            if (female)
                name = Appearance.getFemaleName();

            Townfolk settler = new SettlerBuilder()
                .setName(name)
                .setLocation(((Player) sender).getLocation())
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

            steward.getSettler().getNpc().getOrAddTrait(ArchitectTrait.class);

            HologramTrait hologramTrait = steward.getSettler().getNpc().getOrAddTrait(HologramTrait.class);
            hologramTrait.addLine("&7[&6" + steward.getStewardType().getName() + "&7]");

            if (female) {
                Appearance.applyFemaleStewardSkin(steward);
            } else {
                Appearance.applyMaleStewardSkin(steward);
            }

            StewardLookup.get().registerSteward(steward);

            settler.spawn();
        } catch (InvalidStewardException e) {
            throw new RuntimeException(e);
        }
    }
}
