package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmDismissGui {

    public static Gui createGui(Steward steward, Player player) {
        Gui gui = Gui.gui().title(Component.text("Dismiss steward"))
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
        dismissMeta.displayName(ColorParser.of("<red>Dismiss steward").build());
        dismissMeta.lore(List.of(ColorParser.of("<red>This action is permanent and cannot be undone.").build()));
        dismissMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        dismissItem.setItemMeta(dismissMeta);

        ItemStack backItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of("<red>Cancel").build());
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        gui.setItem(1, 2, ItemBuilder.from(dismissItem).asGuiItem(e -> {
            gui.close(player);
            steward.getSettler().getNpc().destroy();
            TownMetaData.setUnhiredSteward(TownyAPI.getInstance().getTown(player), false);
        }));

        gui.setItem(1, 4, ItemBuilder.from(backItem).asGuiItem(e -> {
            StewardBaseGui.createBaseGui(steward, player).open(player);
        }));
    }

}
