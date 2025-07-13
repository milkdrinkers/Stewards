package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.steward.StewardTypeRegistry;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Logger;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        StewardTrait trait = steward.getSettler().getNpc().getTraitNullable(StewardTrait.class);
        if (trait == null) return;

        gui.setItem(0, ItemBuilder.from(backItem).asGuiItem(e -> {
            steward.stopFollowing(player);
            steward.getSettler().getNpc().getNavigator().setTarget(trait.getAnchorLocation());
            gui.close(player);
        }));

        gui.setItem(2, ItemBuilder.from(stayItem).asGuiItem(e -> {
            if (checkTownBlock(steward, player)) {
                steward.stopFollowing(player, true);
                gui.close(player);
            }

        }));

        gui.setItem(4, ItemBuilder.from(continueItem).asGuiItem(e -> {
            gui.close(player);
        }));
    }

    private static boolean checkTownBlock(Steward steward, Player player) {
        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) { // Shouldn't be possible, considering the player was allowed to interact with the steward.
            Logger.get().error("Something went wrong when checking town block for {}. Town was null.", player.getName());
            return false;
        }

        Chunk chunk = steward.getSettler().getNpc().getEntity().getChunk();

        if (TownMetaData.hasArchitect(town) && !steward.getStewardType().getId().equals(Stewards.getInstance().getStewardTypeHandler().ARCHITECT_ID)) {
            if (StewardLookup.get().getSteward(TownMetaData.getArchitect(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }

        if (TownMetaData.hasBailiff(town) && !steward.getStewardType().getId().equals(Stewards.getInstance().getStewardTypeHandler().BAILIFF_ID)) {
            if (StewardLookup.get().getSteward(TownMetaData.getBailiff(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }

        if (TownMetaData.hasPortmaster(town) && !steward.getStewardType().getId().equals(Stewards.getInstance().getStewardTypeHandler().PORTMASTER_ID)) {
            if (StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }

        if (TownMetaData.hasStablemaster(town) && !steward.getStewardType().getId().equals(Stewards.getInstance().getStewardTypeHandler().STABLEMASTER_ID)) {
            if (StewardLookup.get().getSteward(TownMetaData.getStablemaster(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }

        if (TownMetaData.hasTreasurer(town) && !steward.getStewardType().getId().equals(Stewards.getInstance().getStewardTypeHandler().TREASURER_ID)) {
            if (StewardLookup.get().getSteward(TownMetaData.getTreasurer(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }
        return true;
    }

}
