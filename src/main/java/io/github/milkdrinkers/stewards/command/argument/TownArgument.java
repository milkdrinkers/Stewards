package io.github.milkdrinkers.stewards.command.argument;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public final class TownArgument extends AbstractArgument {
    public static Argument<Town> argument(String node) {
        return new CustomArgument<>(new StringArgument(node), info -> {
            final Town town = TownyAPI.getInstance().getTown(info.input());
            if (town == null)
                error("<red>Town not found!");

            return town;
        }).replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
            return TownyAPI.getInstance().getTowns().stream().map(Town::getName).toList();
        }));
    }
}
