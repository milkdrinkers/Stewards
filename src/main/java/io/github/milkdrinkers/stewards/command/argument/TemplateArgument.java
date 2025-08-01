package io.github.milkdrinkers.stewards.command.argument;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;

import java.util.List;

public final class TemplateArgument extends AbstractArgument {
    public static Argument<String> argument(String node) {
        return new CustomArgument<String, String>(new StringArgument(node), info -> {

            return "";
        }).replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {

            return List.of();
        }));
    }
}
