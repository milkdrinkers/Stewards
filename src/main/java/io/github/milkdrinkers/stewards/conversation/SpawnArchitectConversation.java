package io.github.milkdrinkers.stewards.conversation;

import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.settlers.api.settler.SettlerBuilder;
import io.github.milkdrinkers.settlers.api.settler.Townfolk;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.trait.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Appearance;
import io.github.milkdrinkers.wordweaver.Translation;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public class SpawnArchitectConversation {

    private static Player player;
    private static Location spawnLocation;

    public static Prompt getSpawnArchitectPrompt(Player player, Location spawnLocation) {
        SpawnArchitectConversation.player = player;
        SpawnArchitectConversation.spawnLocation = spawnLocation;
        return spawnArchitectPrompt;
    }

    private static final Prompt spawnArchitectPrompt = new FixedSetPrompt("YES", "NO", "yes", "no", "Yes", "No", "y", "n", "Y", "N") {
        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext conversationContext) {
            return Translation.of("traits.spawner.spawn");
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {
            if (s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("y")) {
                try {
                    boolean female = Math.random() > 0.5;
                    String name = Appearance.getMaleName();
                    if (female)
                        name = Appearance.getFemaleName();

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

                    ArchitectTrait architectTrait = steward.getSettler().getNpc().getOrAddTrait(ArchitectTrait.class);
                    architectTrait.setCreateTime(Instant.now());
                    architectTrait.setSpawningPlayer(player.getUniqueId());

                    HologramTrait hologramTrait = steward.getSettler().getNpc().getOrAddTrait(HologramTrait.class);
                    hologramTrait.addLine("&7[&6" + steward.getStewardType().getName() + "&7]");

                    steward.getSettler().getNpc().getOrAddTrait(LookClose.class).setRange(16);

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
            } else {
                player.sendMessage(ColorParser.of(Translation.of("traits.spawner.come-back")).build());
            }
            return Prompt.END_OF_CONVERSATION;
        }
    };

}
