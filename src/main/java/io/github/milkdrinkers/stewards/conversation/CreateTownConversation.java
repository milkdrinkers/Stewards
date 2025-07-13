package io.github.milkdrinkers.stewards.conversation;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.command.TownCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.towny.TownyDataUtil;
import io.github.milkdrinkers.stewards.utility.Logger;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.Component;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateTownConversation {

    public static ConversationPrefix getPrefix = new ConversationPrefix() {
        @Override
        public @NotNull String getPrefix(@NotNull ConversationContext context) {
            return "Architect: ";
        }
    };

    static String townName;
    static Steward steward;

    public static Prompt getNewTownPrompt(Steward steward) {
        CreateTownConversation.steward = steward;
        return newTownPrompt;
    }


    private static final Prompt newTownPrompt = new StringPrompt() {
        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return Translation.of("towny.new-town-name");
        }

        @Override
        public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
            if (TownyAPI.getInstance().getTown(input) != null) {
                Player player = (Player) context.getForWhom();
                player.sendMessage(Component.text(Translation.of("towny.name-taken")));
                return newTownPrompt;
            }
            townName = input;
            return confirmPrompt;
        }
    };

    private static final Prompt confirmPrompt = new FixedSetPrompt("YES", "NO", "yes", "no", "Yes", "No", "y", "n", "Y", "N") {

        @Override
        public @NotNull String getPromptText(@NotNull ConversationContext context) {
            return Translation.of("towny.new-town-confirmation").formatted(townName);
        }

        @Override
        protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
            if (input.equalsIgnoreCase("no")) return Prompt.END_OF_CONVERSATION;

            Player player = (Player) context.getForWhom();

            Resident resident = TownyAPI.getInstance().getResident(player);
            if (resident == null) {
                player.sendMessage(ColorParser.of(Translation.of("error.resident-null")).build());
                Logger.get().error("Something prevented town block claim on town creation. Resident returned null.");
                return Prompt.END_OF_CONVERSATION;
            }

            try {
                TownyDataUtil.addPlayerAndSteward(player, steward);
                TownCommand.newTown(player, townName, resident, false);
            } catch (TownyException e) {
                player.sendMessage(ColorParser.of(Translation.of("error.towny-exception")).build());
                Logger.get().error(e.getMessage());
                TownyDataUtil.removePlayerAndSteward(player);
                return Prompt.END_OF_CONVERSATION;
            }

            return Prompt.END_OF_CONVERSATION;
        }
    };

}
