package io.github.milkdrinkers.stewards.gui.guard;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.guard.Guard;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;


public class GuardGui {

    public static Gui createGui(Guard guard, Player player) {
        Gui gui = Gui.gui().title(Translation.as("gui.guard.common.title"))
            .rows(5)
            .create();

        gui.disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake();

        populateBorders(gui);
        populateButtons(gui, guard, player);
        populateContent(gui, guard, player);

        return gui;
    }

    private static void populateBorders(Gui gui) {
        ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = borderItem.getItemMeta();
        meta.displayName(Component.empty());
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        borderItem.setItemMeta(meta);

        gui.getFiller().fillBorder(PaperItemBuilder.from(borderItem).asGuiItem());
    }

    private static void populateButtons(Gui gui, Guard guard, Player player) {
        ItemStack exitItem = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.displayName(ColorParser.of(Translation.of("gui.general.exit")).build().decoration(TextDecoration.ITALIC, false));
        exitMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        exitItem.setItemMeta(exitMeta);

        gui.setItem(5, 9, PaperItemBuilder.from(exitItem).asGuiItem(event -> gui.close(player)));

        ItemStack appearanceItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta appearanceMeta = appearanceItem.getItemMeta();
        appearanceMeta.displayName(ColorParser.of(Translation.of("gui.general.re-roll")).build().decoration(TextDecoration.ITALIC, false));
        appearanceMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        appearanceItem.setItemMeta(appearanceMeta);

        gui.setItem(5, 1, PaperItemBuilder.from(appearanceItem).asGuiItem()); // TODO open guard appearance GUI

        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.displayName(ColorParser.of(Translation.of("gui.general.info.name")).with("name", guard.getNpc().getName()).build().decoration(TextDecoration.ITALIC, false));
        infoMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        infoItem.setItemMeta(infoMeta);

        gui.setItem(1, 5, PaperItemBuilder.from(infoItem).asGuiItem());

        ItemStack fireItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta fireMeta = fireItem.getItemMeta();
        fireMeta.displayName(ColorParser.of(Translation.of("gui.general.fire")).build().decoration(TextDecoration.ITALIC, false));
        fireMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        fireItem.setItemMeta(fireMeta);

        gui.setItem(1, 7, PaperItemBuilder.from(fireItem).asGuiItem(e -> {

        }));
    }

    /*
    Design notes:   Range in lore of item should update as the range is changed

     */
    private static void populateContent(Gui gui, Guard guard, Player player) { // TODO backend for this
        ItemStack increaseRange = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta increaseMeta = (SkullMeta) increaseRange.getItemMeta();
        PlayerProfile increaseProfile = Bukkit.createProfile(UUID.randomUUID(), "Increase Range");
        increaseProfile.setProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA0MGZlODM2YTZjMmZiZDJjN2E5YzhlYzZiZTUxNzRmZGRmMWFjMjBmNTVlMzY2MTU2ZmE1ZjcxMmUxMCJ9fX0=")); // TODO Not have this be hardcoded
        increaseMeta.setPlayerProfile(increaseProfile);
        increaseMeta.displayName(ColorParser.of(Translation.of("gui.guard.common.items.increase-range")).build().decoration(TextDecoration.ITALIC, false));
        increaseMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        increaseRange.setItemMeta(increaseMeta);

        ItemStack decreaseRange = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta decreaseMeta = (SkullMeta) decreaseRange.getItemMeta();
        PlayerProfile decreaseProfile = Bukkit.createProfile(UUID.randomUUID(), "Decrease Range");
        decreaseProfile.setProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQzNzM0NmQ4YmRhNzhkNTI1ZDE5ZjU0MGE5NWU0ZTc5ZGFlZGE3OTVjYmM1YTEzMjU2MjM2MzEyY2YifX19"));
        decreaseMeta.setPlayerProfile(decreaseProfile);
        decreaseMeta.displayName(ColorParser.of(Translation.of("gui.guard.common.items.decrease-range")).build().decoration(TextDecoration.ITALIC, false));
        decreaseMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        decreaseRange.setItemMeta(decreaseMeta);

        ItemStack chaseRange = new ItemStack(Material.PAPER);
        ItemMeta chaseMeta = chaseRange.getItemMeta();
        chaseMeta.displayName(ColorParser.of(Translation.of("gui.guard.common.items.chase-range.name")).build().decoration(TextDecoration.ITALIC, false));
        chaseMeta.lore(List.of(
            ColorParser.of(Translation.of("gui.guard.common.items.chase-range.lore-1")).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of(Translation.of("gui.guard.common.items.chase-range.lore-2")).with("range", String.valueOf(1)).build().decoration(TextDecoration.ITALIC, false) // TODO placeholder range
        ));
        chaseMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        chaseRange.setItemMeta(chaseMeta);

        ItemStack wanderRange = new ItemStack(Material.PAPER);
        ItemMeta wanderMeta = wanderRange.getItemMeta();
        wanderMeta.displayName(ColorParser.of(Translation.of("gui.guard.common.items.wander-range.name")).build().decoration(TextDecoration.ITALIC, false));
        wanderMeta.lore(List.of(
            ColorParser.of(Translation.of("gui.guard.common.items.wander-range.lore-1")).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of(Translation.of("gui.guard.common.items.wander-range.lore-2")).with("range", String.valueOf(1)).build().decoration(TextDecoration.ITALIC, false) // TODO placeholder range
        ));
        wanderMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        wanderRange.setItemMeta(wanderMeta);
        
        ItemStack meleeRange = new ItemStack(Material.PAPER);
        ItemMeta meleeMeta = meleeRange.getItemMeta();
        meleeMeta.displayName(ColorParser.of(Translation.of("gui.guard.common.items.melee-range.name")).build().decoration(TextDecoration.ITALIC, false));
        meleeMeta.lore(List.of(
            ColorParser.of(Translation.of("gui.guard.common.items.melee-range.lore-1")).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of(Translation.of("gui.guard.common.items.melee-range.lore-2")).with("range", String.valueOf(1)).build().decoration(TextDecoration.ITALIC, false) // TODO placeholder range
        ));
        meleeMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meleeRange.setItemMeta(meleeMeta);

        ItemStack rangedRange = new ItemStack(Material.PAPER);
        ItemMeta rangedMeta = rangedRange.getItemMeta();
        rangedMeta.displayName(ColorParser.of(Translation.of("gui.guard.common.items.ranged-range.name")).build().decoration(TextDecoration.ITALIC, false));
        rangedMeta.lore(List.of(
            ColorParser.of(Translation.of("gui.guard.common.items.ranged-range.lore-1")).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of(Translation.of("gui.guard.common.items.ranged-range.lore-2")).with("range", String.valueOf(1)).build().decoration(TextDecoration.ITALIC, false) // TODO placeholder range
        ));
        rangedMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        rangedRange.setItemMeta(rangedMeta);
        
        // Chase range controls
        gui.setItem(2, 2, PaperItemBuilder.from(increaseRange).asGuiItem(e -> {
            boolean change = guard.getTrait().increaseChaseRange();
            if (change) {
                createGui(guard, player).open(player);
            }
        }));
        gui.setItem(3, 2, PaperItemBuilder.from(chaseRange).asGuiItem());
        gui.setItem(4, 2, PaperItemBuilder.from(decreaseRange).asGuiItem(e -> {
            boolean change = guard.getTrait().decreaseChaseRange();
            if (change) {
                createGui(guard, player).open(player);
            }
        }));

        // Wander range controls
        gui.setItem(2, 3, PaperItemBuilder.from(increaseRange).asGuiItem(e -> {
            boolean change = guard.getTrait().increaseWanderRange();
            if (change) {
                createGui(guard, player).open(player);
            }
        }));
        gui.setItem(3, 3, PaperItemBuilder.from(wanderRange).asGuiItem());
        gui.setItem(4, 3, PaperItemBuilder.from(decreaseRange).asGuiItem(e -> {
            boolean change = guard.getTrait().decreaseWanderRange();
            if (change) {
                createGui(guard, player).open(player);
            }
        }));
        
        // Melee range controls 
        gui.setItem(2, 7, PaperItemBuilder.from(increaseRange).asGuiItem(e -> {
            boolean change = guard.getTrait().increaseMeleeRange();
            if (change) {
                createGui(guard, player).open(player);
            }
        }));
        gui.setItem(3, 7, PaperItemBuilder.from(meleeRange).asGuiItem());
        gui.setItem(4, 7, PaperItemBuilder.from(decreaseRange).asGuiItem(e -> {
            boolean change = guard.getTrait().decreaseMeleeRange();
            if (change) {
                createGui(guard, player).open(player);
            }
        }));

        // Ranged range controls
        gui.setItem(2, 8, PaperItemBuilder.from(increaseRange).asGuiItem(e -> {
            boolean change = guard.getTrait().increaseRangedRange();
            if (change) {
                createGui(guard, player).open(player);
            }
        }));
        gui.setItem(3, 8, PaperItemBuilder.from(rangedRange).asGuiItem());
        gui.setItem(4, 8, PaperItemBuilder.from(decreaseRange).asGuiItem(e -> {
            boolean change = guard.getTrait().decreaseRangedRange();
            if (change) {
                createGui(guard, player).open(player);
            }
        }));

        ItemStack targetItem = new ItemStack(Material.COMPARATOR);
        ItemMeta targetMeta = targetItem.getItemMeta();
        targetMeta.displayName(ColorParser.of(Translation.of("gui.guard.common.items.target.name")).build().decoration(TextDecoration.ITALIC, false));
        targetMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        targetItem.setItemMeta(targetMeta);

        gui.setItem(5, 4, PaperItemBuilder.from(targetItem).asGuiItem());

        ItemStack equipmentItem = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta equipmentMeta = equipmentItem.getItemMeta();
        equipmentMeta.displayName(ColorParser.of(Translation.of("gui.guard.common.items.equipment.name")).build().decoration(TextDecoration.ITALIC, false));
        equipmentMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        equipmentItem.setItemMeta(equipmentMeta);

        gui.setItem(5, 6, PaperItemBuilder.from(equipmentItem).asGuiItem());
    }

}
