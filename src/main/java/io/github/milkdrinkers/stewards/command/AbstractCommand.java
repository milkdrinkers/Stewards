package io.github.milkdrinkers.stewards.command;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import io.github.milkdrinkers.colorparser.paper.ColorParser;

public abstract class AbstractCommand {
    public static void error(String message) throws WrapperCommandSyntaxException {
        throw CommandAPIBukkit.failWithAdventureComponent(
            ColorParser.of(message).build()
        );
    }
}
