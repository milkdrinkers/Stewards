package io.github.milkdrinkers.stewards.conversation;

import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.utility.SpawnUtils;
import io.github.milkdrinkers.wordweaver.Translation;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static io.github.milkdrinkers.stewards.steward.StewardTypeHandler.ARCHITECT_ID;

public class SpawnArchitectConversation {

    private static Player player;
    private static Location spawnLocation;

    @Deprecated
    /**
     * @deprecated Deprecated in favor of conditions registered in {@link io.github.milkdrinkers.stewards.quest.BetonQuestHandler}
     */
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
                final StewardType type = Objects.requireNonNull(StewardsAPI.getRegistry().getType(ARCHITECT_ID), "Architect type not found in registry");
                SpawnUtils.createSteward(type, null, player, null);
            } else {
                player.sendMessage(ColorParser.of(Translation.of("traits.spawner.come-back")).build());
            }
            return Prompt.END_OF_CONVERSATION;
        }
    };

}
