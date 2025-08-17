package io.github.milkdrinkers.stewards.gui.guard;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.guard.Guard;
import io.github.milkdrinkers.stewards.gui.StewardBaseGui;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.utility.DeleteUtils;
import io.github.milkdrinkers.stewards.utility.Logger;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmFireGuardGui {

    public static Gui createGui(Guard guard, Player player) {
        Gui gui = Gui.gui().title(ColorParser.of(Translation.of("gui.fire.title")).with("type", "guard").build())
            .type(GuiType.HOPPER)
            .create();

        gui.disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake();

        populateButtons(gui, guard, player);

        return gui;
    }

    private static void populateButtons(Gui gui, Guard guard, Player player) {
        ItemStack fireItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta fireMeta = fireItem.getItemMeta();
        fireMeta.displayName(ColorParser.of(Translation.of("gui.fire.fire")).with("type", "guard").build().decoration(TextDecoration.ITALIC, false));
        fireMeta.lore(List.of(ColorParser.of(Translation.of("gui.fire.fire-lore")).build().decoration(TextDecoration.ITALIC, false)));
        fireMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        fireItem.setItemMeta(fireMeta);

        ItemStack backItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of("<red>Cancel").build().decoration(TextDecoration.ITALIC, false));
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        gui.setItem(1, 2, PaperItemBuilder.from(fireItem).asGuiItem(e -> {
            final Town town = TownyAPI.getInstance().getTown(player);
            if (town == null) { // This should never happen, as player was allowed to interact with guard
                Logger.get().error("Something went wrong when checking town for {}. Town was null.", player.getName());
                return;
            }

            DeleteUtils.dismiss(guard, town, player, true);
            gui.close(player);
        }));

        gui.setItem(1, 4, PaperItemBuilder.from(backItem).asGuiItem(e -> {
            GuardGui.createGui(guard, player).open(player);
        }));
    }
}
