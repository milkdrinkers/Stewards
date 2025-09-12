package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.alathranwars.api.AlathranWarsAPI;
import io.github.alathra.alathranwars.conflict.war.WarController;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.trait.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.BailiffTrait;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.List;

/**
 * This gui should only be opened for hired architects, and should be displayed
 * to residents of a town where the architect is hired.
 */
public class StewardUserGui {
    public static Gui createBaseGui(Steward steward, Player player) {
        Stewards plugin = Stewards.getInstance();

        Gui gui = Gui.gui()
            .title(
                ColorParser.of(steward.getStewardType().settlerPrefix() + " " + steward.getNpc().getName()).build())
            .rows(5)
            .disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake()
            .create();

        GuiUtil.addBorder(gui);
        populateButtons(gui, steward, player);

        return gui;
    }

    private static void populateButtons(Gui gui, Steward steward, Player player) {
        ItemStack exitItem = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.customName(ColorParser.of(Translation.of("gui.general.exit")).build());
        exitItem.setItemMeta(exitMeta);
        GuiUtil.hideTooltip(exitItem);
        gui.setItem(5, 9, PaperItemBuilder.from(exitItem).asGuiItem(event -> gui.close(player)));

        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.customName(ColorParser.of(Translation.of("gui.general.info.name")).with("name", steward.getNpc().getName()).build().decoration(TextDecoration.ITALIC, false));
        infoMeta.lore(List.of(
            ColorParser.of(Translation.of("gui.general.info.lore-1")).with("type", steward.getStewardType().name()).build().decoration(TextDecoration.ITALIC, false)
        ));
        if (!steward.getNpc().hasTrait(ArchitectTrait.class)) {
            infoMeta.lore().add(ColorParser.of(Translation.of("gui.general.info.lore-2")).with("level", String.valueOf(steward.getLevel())).build().decoration(TextDecoration.ITALIC, false));

            if (!steward.getNpc().hasTrait(BailiffTrait.class) && steward.getTrait().isHired()) {
                infoMeta.lore().add(ColorParser.of(Translation.of("gui.general.info.lore-3")).with("cost", String.valueOf(Cfg.get().getInt(steward.getStewardType().name().toLowerCase().replace(" ", "-")
                    + ".daily-cost.level-" + steward.getLevel()))).build().decoration(TextDecoration.ITALIC, false));
            }
        }
        infoItem.setItemMeta(infoMeta);
        GuiUtil.hideTooltip(infoItem);
        gui.setItem(1, 5, PaperItemBuilder.from(infoItem).asGuiItem());
    }

    public static void populateWarButtons(Gui gui, Steward steward, Player player) {
        final Town town = TownyAPI.getInstance().getTown(steward.getTownUUID());
        if (town == null)
            return;

        if (WarController.getInstance().isInAnyWars(town)) {
            ItemStack enlistItem = new ItemStack(Material.GOAT_HORN);
            GuiUtil.name(enlistItem, Translation.as("gui.general.enlist.name"));
            GuiUtil.lore(enlistItem, Translation.asList("gui.general.enlist.lore"));
            GuiUtil.hideTooltip(enlistItem);

            gui.setItem(3, 5, PaperItemBuilder.from(enlistItem).asGuiItem(event -> {
                gui.close(player);
                if (!AlathranWarsAPI.getInstance().checkPlaytime(player)) {
                    final Duration required = AlathranWarsAPI.getInstance().getPlaytimeRequired();
                    final Duration current = AlathranWarsAPI.getInstance().getPlaytime(player);
                    player.sendMessage(
                        ColorParser.of(Translation.of("gui.general.enlist.playtime"))
                            .with("required", String.valueOf(required.toHours()))
                            .with("current-hours", String.valueOf(current.toHours()))
                            .with("current-minutes", String.valueOf(current.toMinutesPart()))
                            .build()
                    );
                    return;
                }

                final boolean enlisted = AlathranWarsAPI.getInstance().enlist(player); // Enlists the player in eligible wars
                if (enlisted) {
                    player.sendMessage(ColorParser.of(Translation.of("gui.general.enlist.success")).build());
                } else {
                    player.sendMessage(ColorParser.of(Translation.of("gui.general.enlist.failed")).build());
                }
            }));
        }
    }

    public static void populateWarButtonsMayor(Gui gui, Steward steward, Player player) {
        final Town town = TownyAPI.getInstance().getTown(steward.getTownUUID());
        if (town == null)
            return;

        // Toggle mercenary status button
        if (!WarController.getInstance().isInAnyWars(town)) {
            ItemStack enlistItem = new ItemStack(Material.MACE);
            GuiUtil.name(enlistItem, Translation.as("gui.general.mercenary.name"));
            GuiUtil.lore(enlistItem,
                Translation.ofList("gui.general.mercenary.lore").stream()
                    .map(s -> ColorParser.of(s)
                        .with("status", AlathranWarsAPI.getInstance().isMercenary(town) ? Translation.as("gui.general.mercenary.status.enabled") : Translation.as("gui.general.mercenary.status.disabled"))
                        .build()
                    )
                    .toList()
            );
            GuiUtil.hideTooltip(enlistItem);

            gui.setItem(2, 5, PaperItemBuilder.from(enlistItem).asGuiItem(event -> {
                gui.close(player);
                if (!AlathranWarsAPI.getInstance().canToggleMercenary(town)) {
                    player.sendMessage(
                        Translation.as("gui.general.mercenary.failed")
                    );
                    return;
                }

                player.sendMessage(ColorParser.of(Translation.of("gui.general.enlist.success")).build());
                AlathranWarsAPI.getInstance().setMercenary(town, !AlathranWarsAPI.getInstance().isMercenary(town));
            }));
        }
    }
}
