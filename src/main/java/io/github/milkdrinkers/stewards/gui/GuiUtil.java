package io.github.milkdrinkers.stewards.gui;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public final class GuiUtil {
    public static void name(ItemStack item, ComponentLike component) {
        //noinspection UnstableApiUsage
        item.setData(DataComponentTypes.ITEM_NAME, component.asComponent());
    }

    public static void lore(ItemStack item, ComponentLike component) {
        lore(item, List.of(component));
    }

    public static void lore(ItemStack item, ComponentLike... lore) {
        lore(item, Arrays.stream(lore).toList());
    }

    public static void lore(ItemStack item, List<? extends ComponentLike> lore) {
        //noinspection UnstableApiUsage
        item.setData(DataComponentTypes.LORE, ItemLore.lore().lines(lore));
    }

    public static void lore(ItemStack item) {
        //noinspection UnstableApiUsage
        item.setData(DataComponentTypes.LORE, ItemLore.lore().build());
    }

    public static void hideTooltip(ItemStack item) {
        //noinspection UnstableApiUsage
        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
    }

    public static void addBorder(Gui gui) {
        final ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        GuiUtil.hideTooltip(borderItem);
        gui.getFiller().fillBorder(PaperItemBuilder.from(borderItem).asGuiItem());
    }
}
