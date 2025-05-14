package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.*;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.stewards.utility.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmHireGui {

    public static Gui createGui(Steward steward, Player player, int cost) {
        Gui gui = Gui.gui().title(Component.text("Hire " + steward.getStewardType().getName()))
            .type(GuiType.HOPPER)
            .create();

        gui.disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake();

        populateButtons(gui, steward, player, cost);

        return gui;
    }

    private static void populateButtons(Gui gui, Steward steward, Player player, int cost) {
        ItemStack hireItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta hireMeta = hireItem.getItemMeta();
        hireMeta.displayName(ColorParser.of("<green>Hire " + steward.getStewardType().getName()).build());
        hireMeta.lore(List.of(ColorParser.of("<grey>Hiring this steward costs " + cost + "⊚.").build()));
        hireMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        hireItem.setItemMeta(hireMeta);

        ItemStack backItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of("<red>Back").build());
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        gui.setItem(1, 2, ItemBuilder.from(hireItem).asGuiItem(e -> {
            if (steward.getSettler().getNpc().hasTrait(PortmasterTrait.class)) {
                if (!waterInChunk(steward)) {
                    player.sendMessage(ColorParser.of("<red>The Port Master needs to be closer to water.").build());
                    gui.close(player);
                    return;
                }
            }

            if (checkTownBlock(steward, player)) {
                if (checkTownBank(steward, player, cost)) {
                    player.sendMessage(ColorParser.of("<green>You have successfully hired the " + steward.getStewardType().getName() + ".").build());
                    steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).hire();
                    steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).setTownUUID(TownyAPI.getInstance().getTown(player).getUUID());
                    steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).setLevel(1);
                    steward.getSettler().getNpc().getTraitNullable(StewardTrait.class)
                        .setTownBlock(TownyAPI.getInstance().getTownBlock(steward.getSettler().getNpc().getEntity().getLocation()));

                    steward.setTownUUID(TownyAPI.getInstance().getTown(player).getUUID());
                    steward.setTownBlock(TownyAPI.getInstance().getTownBlock(steward.getSettler().getNpc().getEntity().getLocation()));
                    steward.setLevel(1);

                    TownMetaData.setUnhiredSteward(TownyAPI.getInstance().getTown(player), false);

                    Town town = TownyAPI.getInstance().getTown(player);
                    if (town == null) { // This should never happen, as player was allowed to interact with steward
                        Logger.get().error("Something went wrong when checking town for {}. Town was null.", player.getName());
                        return;
                    }

                    if (steward.getSettler().getNpc().hasTrait(BailiffTrait.class)) {
                        town.getAccount().withdraw(cost, "Stewards: Hired " + steward.getStewardType().getName());
                        TownMetaData.setBailiff(TownyAPI.getInstance().getTown(player), steward);

                        TownyAPI.getInstance().getTown(steward.getTownUUID()).addBonusBlocks(Cfg.get().getInt("bailiff.claims.level-1"));
                    } else if (steward.getSettler().getNpc().hasTrait(PortmasterTrait.class)) {
                        town.getAccount().withdraw(cost, "Stewards: Hired " + steward.getStewardType().getName());
                        TownMetaData.setPortmaster(TownyAPI.getInstance().getTown(player), steward);

                        PortsAPI.createAbstractPort(TownyAPI.getInstance().getTownName(player), steward.getSettler().getNpc().getEntity().getLocation());
                    } else if (steward.getSettler().getNpc().hasTrait(StablemasterTrait.class)) {
                        town.getAccount().withdraw(cost, "Stewards: Hired " + steward.getStewardType().getName());
                        TownMetaData.setStablemaster(TownyAPI.getInstance().getTown(player), steward);

                        PortsAPI.createAbstractCarriageStation(TownyAPI.getInstance().getTownName(player), steward.getSettler().getNpc().getEntity().getLocation());
                    } else if (steward.getSettler().getNpc().hasTrait(TreasurerTrait.class)) {
                        town.getAccount().withdraw(cost, "Stewards: Hired " + steward.getStewardType().getName());
                        TownMetaData.setTreasurer(TownyAPI.getInstance().getTown(player), steward);
                        TownMetaData.setBankLimit(TownyAPI.getInstance().getTown(player), Cfg.get().getInt("treasurer.limit.level-1"));
                    } else { // This should never happen.
                        player.sendMessage(ColorParser.of("<red>Something went wrong, the steward couldn't be saved to town metadata.").build());
                    }
                } else {
                    player.sendMessage(ColorParser.of("<red>There's not enough money in your town bank to hire this steward.").build());
                }
            } else {
                player.sendMessage(ColorParser.of("<red>This steward is too close to another steward.").build());
            }
            gui.close(player);
        }));

        gui.setItem(1, 4, ItemBuilder.from(backItem).asGuiItem(e -> {
            StewardBaseGui.createBaseGui(steward, player).open(player);
        }));
    }

    private static boolean waterInChunk(Steward steward) {
        Chunk chunk = steward.getSettler().getNpc().getEntity().getChunk();

        List<String> biomeList = Cfg.get().getStringList("portmaster.allowed-biomes");

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (biomeList.contains(chunk.getBlock(x, 64, z).getBiome().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkTownBlock(Steward steward, Player player) {
        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) { // Shouldn't be possible, considering the player was allowed to interact with the steward.
            Logger.get().error("Something went wrong when checking town block for {}. Town was null.", player.getName());
            return false;
        }

        Chunk chunk = steward.getSettler().getNpc().getEntity().getChunk();

        if (TownMetaData.hasArchitect(town)) {
            if (StewardLookup.get().getSteward(TownMetaData.getArchitect(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }

        if (TownMetaData.hasBailiff(town)) {
            if (StewardLookup.get().getSteward(TownMetaData.getBailiff(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }

        if (TownMetaData.hasPortmaster(town)) {
            if (StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }

        if (TownMetaData.hasStablemaster(town)) {
            if (StewardLookup.get().getSteward(TownMetaData.getStablemaster(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }

        if (TownMetaData.hasTreasurer(town)) {
            if (StewardLookup.get().getSteward(TownMetaData.getTreasurer(town)).getSettler().getNpc().getEntity().getChunk().getChunkKey() == chunk.getChunkKey())
                return false;
        }
        return true;
    }

    private static boolean checkTownBank(Steward steward, Player player, int cost) {
        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) { // Shouldn't be possible, considering the player was allowed to interact with the steward.
            Logger.get().error("Something went wrong when checking town bank for {}. Town was null.", player.getName());
            return false;
        }

        return town.getAccount().canPayFromHoldings(cost);
    }

}
