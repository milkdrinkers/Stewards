package io.github.milkdrinkers.stewards.gui.guard;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.gui.StewardBaseGui;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GuardCaptainGui {

    public static Gui createGui(Steward steward, Player player) {
        Gui gui = Gui.gui().title(Component.text(Translation.of("gui.guard.captain.title")))
            .rows(5)
            .create();

        gui.disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake();

        populateBorders(gui);
        populateButtons(gui, steward, player);

        return gui;
    }

    private static void populateBorders(Gui gui) {
        ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = borderItem.getItemMeta();
        meta.displayName(Component.empty());
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        borderItem.setItemMeta(meta);

        gui.getFiller().fillBorder(ItemBuilder.from(borderItem).asGuiItem());
    }

    private static void populateButtons(Gui gui, Steward steward, Player player) {
        ItemStack backItem = new ItemStack(Material.PAPER);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of(Translation.of("gui.general.back")).build().decoration(TextDecoration.ITALIC, false));
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        gui.setItem(5, 9, ItemBuilder.from(backItem).asGuiItem(event -> StewardBaseGui.createBaseGui(steward, player).open(player)));

        populateGuardButtons(gui, steward);
    }

    private static void populateGuardButtons(Gui gui, Steward steward) {

    }

    private static ItemStack guardItem(int guardNumber) {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorParser.of(Translation.of("gui.guard.captain.guard.name-1"))
            .with("number", String.valueOf(guardNumber)).build());
        meta.lore(List.of(ColorParser.of(Translation.of("gui.guard.captain.guard.lore-3")).build()));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack spawnGuardItem(int guardNumber) {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorParser.of(Translation.of("gui.guard.captain.guard.name-2"))
            .with("number", String.valueOf(guardNumber)).build());
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack lockedGuardItem(int guardNumber) {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(ColorParser.of(Translation.of("gui.guard.captain.guard.name-3"))
            .with("number", String.valueOf(guardNumber)).build());
        meta.lore(List.of(ColorParser.of(Translation.of("gui.guard.captain.guard.lore-3")).build()));
        item.setItemMeta(meta);
        return item;
    }

}
