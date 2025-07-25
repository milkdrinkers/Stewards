package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.utility.CheckUtils;
import io.github.milkdrinkers.stewards.utility.Logger;
import io.github.milkdrinkers.stewards.utility.SpawnUtils;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmHireGui {

    public static Gui createGui(Steward steward, Player player, int cost) {
        Gui gui = Gui.gui().title(ColorParser.of(Translation.of("gui.hire.title")).with("type", steward.getStewardType().name()).build())
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
        hireMeta.displayName(
            ColorParser.of(Translation.of("gui.hire.hire"))
                .with("type", steward.getStewardType().name())
                .with("price", String.valueOf(cost))
                .build()
                .decoration(TextDecoration.ITALIC, false)
        );
        hireMeta.lore(List.of(ColorParser.of(Translation.of("gui.hire.hire-lore")).with("price", String.valueOf(cost)).build().decoration(TextDecoration.ITALIC, false)));
        hireMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        hireItem.setItemMeta(hireMeta);

        ItemStack backItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.displayName(ColorParser.of(Translation.of("gui.general.back")).build().decoration(TextDecoration.ITALIC, false));
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        backItem.setItemMeta(backMeta);

        gui.setItem(1, 2, PaperItemBuilder.from(hireItem).asGuiItem(e -> {
            final Town town = TownyAPI.getInstance().getTown(player);
            if (town == null) { // This should never happen, as player was allowed to interact with steward
                Logger.get().error("Something went wrong when checking town for {}. Town was null.", player.getName());
                gui.close(player);
                return;
            }

            if (!CheckUtils.canAfford(town, cost)) {
                player.sendMessage(ColorParser.of("<red>You cannot afford to hire this steward!").build()); // TODO Translate
                gui.close(player);
                return;
            }

            SpawnUtils.hireSteward(steward, town, player, cost, true);
            gui.close(player);
        }));

        gui.setItem(1, 4, PaperItemBuilder.from(backItem).asGuiItem(e -> {
            StewardBaseGui.createBaseGui(steward, player).open(player);
        }));
    }
}
