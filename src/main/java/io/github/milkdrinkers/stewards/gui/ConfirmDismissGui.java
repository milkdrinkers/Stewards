package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.utility.DeleteUtils;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmDismissGui {

    public static Gui createGui(Steward steward, Player player) {
        Gui gui = Gui.gui().title(Translation.as("gui.dismiss.title"))
            .type(GuiType.HOPPER)
            .create();

        gui.disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake();

        populateButtons(gui, steward, player);

        return gui;
    }

    private static void populateButtons(Gui gui, Steward steward, Player player) {
        ItemStack dismissItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta dismissMeta = dismissItem.getItemMeta();
        dismissMeta.displayName(ColorParser.of(Translation.of("gui.dismiss.dismiss-steward")).build().decoration(TextDecoration.ITALIC, false));
        dismissMeta.lore(List.of(ColorParser.of(Translation.of("gui.dismiss.dismiss-steward-lore")).build().decoration(TextDecoration.ITALIC, false)));
        dismissMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        dismissItem.setItemMeta(dismissMeta);

        ItemStack backItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of(Translation.of("gui.general.cancel")).build().decoration(TextDecoration.ITALIC, false));
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        gui.setItem(1, 2, ItemBuilder.from(dismissItem).asGuiItem(e -> {
            gui.close(player);
            DeleteUtils.dismiss(steward, TownyAPI.getInstance().getTown(player), player, true);
        }));

        gui.setItem(1, 4, ItemBuilder.from(backItem).asGuiItem(e -> {
            StewardBaseGui.createBaseGui(steward, player).open(player);
        }));
    }

}
