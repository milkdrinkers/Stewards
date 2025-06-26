package io.github.milkdrinkers.stewards.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmMoveGui {

    public static Gui createGui(Steward steward, Player player) {
        Gui gui = Gui.gui().title(ColorParser.of(Translation.of("gui.move.title")).build())
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
        ItemStack backItem = new ItemStack(Material.RED_BED);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of(Translation.of("gui.move.go-back")).build().decoration(TextDecoration.ITALIC, false));
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        ItemStack stayItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta stayMeta = stayItem.getItemMeta();
        stayMeta.displayName(ColorParser.of(Translation.of("gui.move.stay")).build().decoration(TextDecoration.ITALIC, false));
        stayMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        stayItem.setItemMeta(stayMeta);

        ItemStack continueItem = new ItemStack(Material.LEAD);
        ItemMeta continueMeta = continueItem.getItemMeta();
        continueMeta.displayName(ColorParser.of(Translation.of("gui.move.follow")).build().decoration(TextDecoration.ITALIC, false));
        continueMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        continueItem.setItemMeta(continueMeta);

        StewardTrait trait = steward.getSettler().getNpc().getTraitNullable(StewardTrait.class);
        if (trait == null) return;

        gui.setItem(0, ItemBuilder.from(backItem).asGuiItem(e -> {
            steward.stopFollowing(player);
            steward.getSettler().getNpc().getNavigator().setTarget(trait.getAnchorLocation());
            gui.close(player);
        }));

        gui.setItem(2, ItemBuilder.from(stayItem).asGuiItem(e -> {
            steward.stopFollowing(player);
            trait.setAnchorLocation(steward.getSettler().getNpc().getEntity().getLocation());
            StewardLookup.get().removeStewardFollowingPlayer(player);

            gui.close(player);
        }));

        gui.setItem(4, ItemBuilder.from(continueItem).asGuiItem(e -> {
            gui.close(player);
        }));
    }

}
