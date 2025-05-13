package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.PortmasterTrait;
import io.github.milkdrinkers.stewards.trait.StablemasterTrait;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.trait.TreasurerTrait;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.stewards.utility.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmStipendGui {

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
        ItemStack upgradeItem = new ItemStack(Material.PAPER);
        ItemMeta upgradeMeta = upgradeItem.getItemMeta();
        upgradeMeta.displayName(ColorParser.of("<green>Hire " + steward.getStewardType().getName()).build());
        upgradeMeta.lore(List.of(ColorParser.of("<grey>Hiring this steward costs " + cost + "⊚.").build()));
        upgradeMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        upgradeItem.setItemMeta(upgradeMeta);

        ItemStack backItem = new ItemStack(Material.PAPER);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of("<red>Back").build());
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        gui.setItem(1, 2, ItemBuilder.from(upgradeItem).asGuiItem(e -> {
            if (checkTownBank(player, cost)) {
                player.sendMessage(ColorParser.of("<green>You have successfully paid the " + steward.getStewardType().getName() + ".").build());
                steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).setStriking(false);

                Town town = TownyAPI.getInstance().getTown(player);
                if (town == null) { // This should never happen, as player was allowed to interact with steward
                    Logger.get().error("Something went wrong when checking town for {}. Town was null.", player.getName());
                    return;
                }

                if (steward.getSettler().getNpc().hasTrait(PortmasterTrait.class)) {
                    town.getAccount().withdraw(cost, "Stewards: Paid " + steward.getStewardType().getName());
                    TownMetaData.setPortmaster(TownyAPI.getInstance().getTown(player), steward);
                    PortsAPI.setBlockaded(PortsAPI.getPortFromTown(town), false);
                } else if (steward.getSettler().getNpc().hasTrait(StablemasterTrait.class)) {
                    town.getAccount().withdraw(cost, "Stewards: Paid " + steward.getStewardType().getName());
                    TownMetaData.setStablemaster(TownyAPI.getInstance().getTown(player), steward);
                    PortsAPI.setBlockaded(PortsAPI.getCarriageStationFromTown(town), false);
                } else if (steward.getSettler().getNpc().hasTrait(TreasurerTrait.class)) {
                    town.getAccount().withdraw(cost, "Stewards: Paid " + steward.getStewardType().getName());
                    TownMetaData.setTreasurer(TownyAPI.getInstance().getTown(player), steward);
                    TownMetaData.setBankLimit(TownyAPI.getInstance().getTown(player), Cfg.get().getInt("treasurer.limit.level-" + steward.getLevel()));
                } else { // This should never happen.
                    player.sendMessage(ColorParser.of("<red>Something went wrong, the steward couldn't be paid.").build());
                }
            } else {
                player.sendMessage(ColorParser.of("<red>There's not enough money in your town bank to pay this steward.").build());
            }
            gui.close(player);
        }));

        gui.setItem(1, 4, ItemBuilder.from(backItem).asGuiItem(e -> {
            StewardBaseGui.createBaseGui(steward, player).open(player);
        }));
    }

    private static boolean checkTownBank(Player player, int cost) {
        Town town = TownyAPI.getInstance().getTown(player);
        if (town == null) { // Shouldn't be possible, considering the player was allowed to interact with the steward.
            Logger.get().error("Something went wrong when checking town bank for {}. Town was null.", player.getName());
            return false;
        }

        return town.getAccount().canPayFromHoldings(cost);
    }

}
