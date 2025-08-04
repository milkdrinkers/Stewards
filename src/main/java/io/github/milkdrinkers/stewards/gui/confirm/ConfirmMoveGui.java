package io.github.milkdrinkers.stewards.gui;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.trait.traits.StewardTrait;
import io.github.milkdrinkers.stewards.utility.SpawnUtils;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static io.github.milkdrinkers.stewards.steward.StewardTypeHandler.ARCHITECT_ID;

public class ConfirmMoveGui {

    public static Gui createGui(Steward steward, Player player) {
        Gui gui = Gui.gui().title(ColorParser.of(Translation.of("gui.following.title")).build())
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
        backMeta.displayName(ColorParser.of(Translation.of("gui.following.go-back")).build().decoration(TextDecoration.ITALIC, false));
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        ItemStack stayItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta stayMeta = stayItem.getItemMeta();
        stayMeta.displayName(ColorParser.of(Translation.of("gui.following.stay")).build().decoration(TextDecoration.ITALIC, false));
        stayMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        stayItem.setItemMeta(stayMeta);

        ItemStack continueItem = new ItemStack(Material.LEAD);
        ItemMeta continueMeta = continueItem.getItemMeta();
        continueMeta.displayName(ColorParser.of(Translation.of("gui.following.follow")).build().decoration(TextDecoration.ITALIC, false));
        continueMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        continueItem.setItemMeta(continueMeta);

        final StewardTrait trait = steward.getTrait();
        final StewardType architectType = StewardsAPI.getRegistry().getType(ARCHITECT_ID);
        final boolean isArchitect = steward.getStewardType().equals(architectType);

        gui.setItem(0, PaperItemBuilder
            .from(Material.RED_BED)
            .name(ColorParser.of(Translation.of("gui.following.go-back")).build().decoration(TextDecoration.ITALIC, false))
            .flags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            .asGuiItem(e -> {
                if (steward.isFounder()) {
                    steward.stopFollowing(player, true);
                } else {
                    steward.stopFollowing(player);
                }
                steward.getNpc().getNavigator().setTarget(trait.getAnchorLocation());
                gui.close(player);
            }));

        gui.setItem(2, PaperItemBuilder
            .from(Material.GRASS_BLOCK)
            .name(ColorParser.of(Translation.of("gui.following.stay")).build().decoration(TextDecoration.ITALIC, false))
            .flags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            .asGuiItem(e -> {
                gui.close(player);
                if (!SpawnUtils.testPortMasterLocation().test(steward)) {
                    player.sendMessage(ColorParser.of("<red>The Port Master needs to be closer to water.").build()); // TODO Translate
                    return;
                }

                if (SpawnUtils.testStewardLocationMove().test(steward)) {
                    steward.stopFollowing(player, true);
                } else {
                    player.sendMessage(ColorParser.of(Translation.of("gui.hire.too-close")).build());
                }
            }));

        gui.setItem(4, PaperItemBuilder
            .from(Material.LEAD)
            .name(ColorParser.of(Translation.of("gui.following.follow")).build().decoration(TextDecoration.ITALIC, false))
            .flags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            .asGuiItem(e -> {
                gui.close(player);
            }));
    }
}
