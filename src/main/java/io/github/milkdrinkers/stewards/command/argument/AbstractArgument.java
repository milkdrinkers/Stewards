package io.github.milkdrinkers.stewards.command.argument;

import dev.jorel.commandapi.arguments.CustomArgument;
import io.github.milkdrinkers.colorparser.paper.ColorParser;

public abstract class AbstractArgument {
    public static void error(String message) throws CustomArgument.CustomArgumentException {
        throw CustomArgument.CustomArgumentException.fromAdventureComponent(
            ColorParser.of(message).build()
        );
    }
}
