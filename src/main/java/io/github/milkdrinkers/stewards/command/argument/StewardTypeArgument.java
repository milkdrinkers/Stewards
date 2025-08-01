package io.github.milkdrinkers.stewards.command.argument;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.StewardType;

public final class StewardTypeArgument extends AbstractArgument {
    public static Argument<StewardType> argument(String node) {
        return new CustomArgument<>(new StringArgument(node), info -> {
            final StewardType type = StewardsAPI.getRegistry().getType(info.input());
            if (type == null)
                error("No registry of that type could be found!");

            return type;
        }).replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
            return StewardsAPI.getRegistry().getKeys();
        }));
    }
}
