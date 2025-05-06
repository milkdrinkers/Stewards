package io.github.milkdrinkers.stewards.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmMoveGui {

    public static Gui createGui(Steward steward, Player player) {
        Gui gui = Gui.gui().title(Component.text("Stop following"))
            .type(GuiType.HOPPER)
            .create();

        gui.disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake();

        populateButtons(gui, steward, player);

        return gui;
    }

    private static void populateButtons(Gui gui, Steward steward, Player player){
        ItemStack backItem = new ItemStack(Material.PAPER); // TODO: Placeholder item
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of("<green>Go back!").build());
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        ItemStack stayItem = new ItemStack(Material.PAPER); // TODO: Placeholder item
        ItemMeta stayMeta = stayItem.getItemMeta();
        stayMeta.displayName(ColorParser.of("<green>Stay here!").build());
        stayMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        stayItem.setItemMeta(stayMeta);

        ItemStack continueItem = new ItemStack(Material.PAPER); // TODO: Placeholder item
        ItemMeta continueMeta = continueItem.getItemMeta();
        continueMeta.displayName(ColorParser.of("<green>Keep following!").build());
        continueMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        continueItem.setItemMeta(continueMeta);

        StewardTrait trait = steward.getSettler().getNpc().getTraitNullable(StewardTrait.class);
        if (trait == null) return;

        gui.setItem(0, ItemBuilder.from(backItem).asGuiItem(e -> {
            steward.getSettler().getNpc().getNavigator().setTarget(trait.getAnchorLocation());

            trait.setFollowing(false);
            trait.setFollowingPlayer(null);

            StewardLookup.get().removeStewardFollowingPlayer(player);

            gui.close(player);
        }));

        gui.setItem(2, ItemBuilder.from(stayItem).asGuiItem(e -> {
            steward.getSettler().getNpc().getNavigator().cancelNavigation();


            trait.setAnchorLocation(steward.getSettler().getNpc().getEntity().getLocation());
            trait.setFollowing(false);
            trait.setFollowingPlayer(null);

            StewardLookup.get().removeStewardFollowingPlayer(player);

            gui.close(player);
        }));

        gui.setItem(4, ItemBuilder.from(continueItem).asGuiItem(e -> {
            gui.close(player);
        }));
    }

}
