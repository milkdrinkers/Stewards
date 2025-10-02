package io.github.milkdrinkers.stewards.gui.confirm;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.alathra.alathraports.core.carriagestations.CarriageStation;
import io.github.alathra.alathraports.core.ports.Port;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.gui.StewardBaseGui;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.stewards.utility.CheckUtils;
import io.github.milkdrinkers.stewards.utility.Logger;
import io.github.milkdrinkers.wordweaver.Translation;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmUpgradeGui {

    public static Gui createGui(Steward steward, Player player, int cost) {
        Gui gui = Gui.gui().title(ColorParser.of(Translation.of("gui.upgrade.title")).with("type", steward.getStewardType().name()).build())
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
        ItemStack upgradeItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta upgradeMeta = upgradeItem.getItemMeta();
        upgradeMeta.displayName(ColorParser.of(Translation.of("gui.upgrade.upgrade")).with("type", steward.getStewardType().name()).build().decoration(TextDecoration.ITALIC, false));
        upgradeMeta.lore(List.of(ColorParser.of(Translation.of("gui.upgrade.upgrade-lore")).with("cost", String.valueOf(cost)).build().decoration(TextDecoration.ITALIC, false)));
        upgradeMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        upgradeItem.setItemMeta(upgradeMeta);

        ItemStack backItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of(Translation.of("gui.general.back")).build().decoration(TextDecoration.ITALIC, false));
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        gui.setItem(1, 2, PaperItemBuilder.from(upgradeItem).asGuiItem(e -> {
            final Town town = TownyAPI.getInstance().getTown(player);
            if (town == null) {
                player.sendMessage(ColorParser.of(Translation.of("error.towny-exception")).build());
                Logger.get().error("Something went wrong when checking town for {}. Town was null.", player.getName());
                gui.close(player);
                return;
            }

            if (!CheckUtils.canAfford(town, cost)) {
                player.sendMessage(ColorParser.of(Translation.of("gui.upgrade.not-enough-funds")).build());
                gui.close(player);
                return;
            }

            if (steward.getTrait().levelUp()) {
                player.sendMessage(ColorParser.of(Translation.of("gui.upgrade.upgrade-success"))
                    .with("type", steward.getStewardType().name())
                    .with("level", String.valueOf(steward.getLevel()))
                    .build()
                );

                if (steward.getStewardType().id().equals(StewardTypeHandler.BAILIFF_ID)) {

                    town.addBonusBlocks((Cfg.get().getInt("bailiff.claims.level-" + steward.getLevel()) -
                        Cfg.get().getInt("bailiff.claims.level-" + (steward.getLevel()))));

                } else if (steward.getStewardType().id().equals(StewardTypeHandler.PORTMASTER_ID)) {

                    Port port = PortsAPI.getPortFromTown(town);
                    if (port == null) { // This shouldn't be possible
                        Logger.get().error("Something went wrong when upgrading steward {}. Port was null.", steward.getSettler().getNpc().getName());
                        player.sendMessage(ColorParser.of(Translation.of("error.no-upgrade")).build());
                        gui.close(player);
                        return;
                    }
                    PortsAPI.upgradePort(port);

                } else if (steward.getStewardType().id().equals(StewardTypeHandler.STABLEMASTER_ID)) {

                    CarriageStation station = PortsAPI.getCarriageStationFromTown(town);
                    if (station == null) {
                        Logger.get().error("Something went wrong when upgrading steward {}. CarriageStation was null.", steward.getSettler().getNpc().getName());
                        player.sendMessage(ColorParser.of(Translation.of("error.no-upgrade")).build());
                        gui.close(player);
                        return;
                    }
                    PortsAPI.upgradeCarriageStation(station);

                } else if (steward.getStewardType().id().equals(StewardTypeHandler.TREASURER_ID)) {

                    TownMetaData.setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-" + steward.getLevel()));

                }

                CheckUtils.pay(town, cost, "Stewards: Upgraded " + steward.getStewardType().name());

                final HologramTrait hologramTrait = steward.getSettler().getNpc().getOrAddTrait(HologramTrait.class);
                hologramTrait.clear();
                hologramTrait.addLine("&7[&b" + steward.getStewardType().name() + "&7]" + " &aLvl " + steward.getLevel());

            } else {
                Logger.get().error("Something went wrong when upgrading steward {}. Level was not upgraded.", steward.getSettler().getNpc().getName());
                player.sendMessage(ColorParser.of(Translation.of("error.no-upgrade")).build());
            }
            gui.close(player);
        }));

        gui.setItem(1, 4, PaperItemBuilder.from(backItem).asGuiItem(e -> {
            StewardBaseGui.createBaseGui(steward, player).open(player);
        }));
    }

}
