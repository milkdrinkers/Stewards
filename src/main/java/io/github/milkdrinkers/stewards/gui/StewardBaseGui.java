package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.colorparser.ColorParser;
import io.github.milkdrinkers.settlers.api.settler.Companion;
import io.github.milkdrinkers.settlers.api.settler.SettlerBuilder;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.conversation.CreateTownConversation;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.*;
import io.github.milkdrinkers.stewards.utility.Appearance;
import io.github.milkdrinkers.stewards.utility.Cfg;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class StewardBaseGui { // TODO refactor this absolutely disgusting class

    public static Gui createBaseGui(Steward steward, Player player) {
        Stewards plugin = Stewards.getInstance();

        Gui gui = Gui.gui()
            .title(Component.text(steward.getStewardType().getSettlerPrefix()
                + " " + steward.getSettler().getNpc().getName()))
            .rows(5).create();

        gui.disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake();

        populateBorders(gui);
        populateButtons(gui, steward, player);

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(plugin.getStewardTypeHandler().ARCHITECT_ID)) {
            if (steward.getTownUUID() == null)
                populateArchitectNoTownButtons(gui, steward, player);
            else
                populateArchitectTownButtons(gui, steward, player);
        }

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(plugin.getStewardTypeHandler().TREASURER_ID)) {
            if (steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).isHired()) {
                populateHiredButtons(gui, steward, player);
            } else {
                populateUnHiredButtons(gui, steward, player);
            }
        }

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(plugin.getStewardTypeHandler().BAILIFF_ID)) {
            if (steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).isHired()) {
                populateHiredButtons(gui, steward, player);
            } else {
                populateUnHiredButtons(gui, steward, player);
            }
        }

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(plugin.getStewardTypeHandler().PORTMASTER_ID)) {
            if (steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).isHired()) {
                populateHiredButtons(gui, steward, player);
            } else {
                populateUnHiredButtons(gui, steward, player);
            }
        }

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(plugin.getStewardTypeHandler().STABLEMASTER_ID)) {
            if (steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).isHired()) {
                populateHiredButtons(gui, steward, player);
            } else {
                populateUnHiredButtons(gui, steward, player);
            }
        }

        return gui;
    }

    private static void populateBorders(Gui gui) {
        ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = borderItem.getItemMeta();
        meta.displayName(Component.empty());
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        borderItem.setItemMeta(meta);

        gui.getFiller().fillBorder(ItemBuilder.from(borderItem).asGuiItem());
    }

    private static void populateButtons(Gui gui, Steward steward, Player player) {
        ItemStack exitItem = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.displayName(ColorParser.of("<dark_red>Exit").build());
        exitMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        exitItem.setItemMeta(exitMeta);

        gui.setItem(5, 9, ItemBuilder.from(exitItem).asGuiItem(event -> gui.close(player)));

        ItemStack appearanceItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta appearanceMeta = appearanceItem.getItemMeta();
        appearanceMeta.displayName(ColorParser.of("<green>Re-roll name and skin").build());
        appearanceMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        appearanceItem.setItemMeta(appearanceMeta);

        gui.setItem(5, 1, ItemBuilder.from(appearanceItem).asGuiItem(event -> AppearanceGui.createGui(steward, player).open(player)));


        ItemStack followItem = new ItemStack(Material.LEAD);
        ItemMeta followMeta = followItem.getItemMeta();


        if (steward.getSettler().getNpc().getTraitNullable(StewardTrait.class).isFollowing()) { // TODO NO ITALICS IN NAMES
            followMeta.displayName(ColorParser.of("<green>Stop following").build());
        } else {
            followMeta.displayName(ColorParser.of("<green>Follow me").build());
        }

        followMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        followItem.setItemMeta(followMeta);

        gui.setItem(5, 5, ItemBuilder.from(followItem).asGuiItem(event -> {
            StewardTrait trait = steward.getSettler().getNpc().getTraitNullable(StewardTrait.class);
            if (trait == null) return;

            if (trait.isFollowing()) {
                ConfirmMoveGui.createGui(steward, player).open(player);
            } else {
                steward.getSettler().getNpc().getNavigator().setTarget(player, false);
                trait.setFollowing(true);
                trait.setFollowingPlayer(player);
                StewardLookup.get().setStewardFollowingPlayer(player, steward);
                gui.close(player);
            }
        }));
    }

    private static void populateUnHiredButtons(Gui gui, Steward steward, Player player) {
        int cost = Cfg.get().getInt(steward.getStewardType().getName().toLowerCase().replace(" ", "") + ".upgrade-cost.level-1");

        ItemStack hireItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta hireMeta = hireItem.getItemMeta();
        hireMeta.displayName(ColorParser.of("<green>Hire steward").build());
        hireMeta.lore(List.of(ColorParser.of("<grey>Hiring this steward costs <cost>⊚.")
            .parseMinimessagePlaceholder("cost", String.valueOf(cost)).build()));
        hireMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        hireItem.setItemMeta(hireMeta);

        ItemStack dismissItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta dismissMeta = dismissItem.getItemMeta();
        dismissMeta.displayName(ColorParser.of("<red>Dismiss Steward").build());
        dismissMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        dismissItem.setItemMeta(dismissMeta);

        gui.setItem(3, 4, ItemBuilder.from(hireItem).asGuiItem(event -> {
            ConfirmHireGui.createGui(steward, player, cost).open(player);
        }));

        gui.setItem(3, 6, ItemBuilder.from(dismissItem).asGuiItem(event -> {
            ConfirmDismissGui.createGui(steward, player).open(player);
        }));
    }

    private static void populateHiredButtons(Gui gui, Steward steward, Player player) {
        int cost = Cfg.get().getInt(steward.getStewardType().getName().toLowerCase()
            .replace(" ", "") + ".upgrade-cost.level-" + (steward.getLevel() + 1));

        ItemStack upgradeItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta upgradeMeta = upgradeItem.getItemMeta();

        if (steward.getLevel() < steward.getStewardType().getMaxLevel()) {
            upgradeMeta.displayName(ColorParser.of("<green>Upgrade <steward>")
                .parseMinimessagePlaceholder("steward", steward.getStewardType().getName()).build());
            upgradeMeta.lore(List.of(ColorParser.of("<grey>Upgrading this steward costs <cost>⊚.")
                .parseMinimessagePlaceholder("cost", String.valueOf(cost)).build()));
        } else {
            upgradeMeta.displayName(ColorParser.of("<green>" + steward.getStewardType().getName()).build());
            upgradeMeta.lore(List.of(ColorParser.of("<grey>You've reached the maximum level for this steward.").build()));
        }

        upgradeMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        upgradeItem.setItemMeta(upgradeMeta);

        ItemStack fireItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta fireMeta = fireItem.getItemMeta();
        fireMeta.displayName(ColorParser.of("<red>Fire steward").build());
        fireMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        fireItem.setItemMeta(fireMeta);

        if (steward.getStewardType() == Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(Stewards.getInstance().getStewardTypeHandler().PORTMASTER_ID)) {

            if (TownyAPI.getInstance().getResident(player).isMayor()) {
                gui.setItem(3, 3, ItemBuilder.from(upgradeItem).asGuiItem(event -> {
                    if (steward.getLevel() < steward.getStewardType().getMaxLevel()) {
                        ConfirmUpgradeGui.createGui(steward, player, cost).open(player);
                    } else {
                        gui.close(player);
                        player.sendMessage(ColorParser.of("<red>You've reached the maximum level for this steward.").build());
                    }
                }));

                gui.setItem(3, 7, ItemBuilder.from(fireItem).asGuiItem(event -> {
                    ConfirmFireGui.createGui(steward, player).open(player);
                }));
            }

            ItemStack portItem = new ItemStack(Material.OAK_BOAT);
            ItemMeta portMeta = portItem.getItemMeta();
            portMeta.displayName(ColorParser.of("<green>Open travel menu").build());
            portMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            portItem.setItemMeta(portMeta);
            gui.setItem(3, 5, ItemBuilder.from(portItem).asGuiItem(event -> {
                PortsAPI.openTravelMenu(player, PortsAPI.getPortFromTown(TownyAPI.getInstance().getTown(steward.getTownUUID())));
            }));

        } else if (steward.getStewardType() == Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(Stewards.getInstance().getStewardTypeHandler().STABLEMASTER_ID)) {

            if (TownyAPI.getInstance().getResident(player).isMayor()) {
                gui.setItem(3, 3, ItemBuilder.from(upgradeItem).asGuiItem(event -> {
                    ConfirmUpgradeGui.createGui(steward, player, cost).open(player);
                }));

                gui.setItem(3, 7, ItemBuilder.from(fireItem).asGuiItem(event -> {
                    ConfirmFireGui.createGui(steward, player).open(player);
                }));
            }

            ItemStack stationItem = new ItemStack(Material.SADDLE);
            ItemMeta stationMeta = stationItem.getItemMeta();
            stationMeta.displayName(ColorParser.of("<green>Open travel menu").build());
            stationMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            stationItem.setItemMeta(stationMeta);

            gui.setItem(3, 5, ItemBuilder.from(stationItem).asGuiItem(event -> {
                PortsAPI.openTravelMenu(player, PortsAPI.getCarriageStationFromTown(TownyAPI.getInstance().getTown(steward.getTownUUID())));
            }));

        } else {
            gui.setItem(3, 4, ItemBuilder.from(upgradeItem).asGuiItem(event -> {
                ConfirmUpgradeGui.createGui(steward, player, cost).open(player);
            }));

            gui.setItem(3, 6, ItemBuilder.from(fireItem).asGuiItem(event -> {
                ConfirmFireGui.createGui(steward, player).open(player);
            }));
        }
    }

    private static void populateArchitectNoTownButtons(Gui gui, Steward steward, Player player) {
        ItemStack townItem = new ItemStack(Material.RED_BED);
        ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(ColorParser.of("<green>Create town").build());
        townMeta.lore(List.of(ColorParser.of("<grey>Creating a town costs " + Math.round(TownySettings.getNewTownPrice()) + "⊚").build()));
        townMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        townItem.setItemMeta(townMeta);

        ItemStack dismissItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta dismissMeta = dismissItem.getItemMeta();
        dismissMeta.displayName(ColorParser.of("<red>Dismiss Steward").build());
        dismissMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        dismissItem.setItemMeta(dismissMeta);

        gui.setItem(3, 4, ItemBuilder.from(townItem).asGuiItem(event -> {
            gui.close(player);
            ConversationFactory factory = new ConversationFactory(Stewards.getInstance()).withPrefix(CreateTownConversation.getPrefix).withLocalEcho(false);
            factory.withFirstPrompt(CreateTownConversation.getNewTownPrompt(steward)).buildConversation(player).begin();
        }));

        gui.setItem(3, 6, ItemBuilder.from(dismissItem).asGuiItem(event -> {
            ConfirmDismissGui.createGui(steward, player).open(player);
        }));
    }

    private static void populateArchitectTownButtons(Gui gui, Steward steward, Player player) {
        boolean unhiredSteward = TownMetaData.hasUnhiredSteward(TownyAPI.getInstance().getTown(player));
        Town town = TownyAPI.getInstance().getTown(player);

        ItemStack treasurerItem = new ItemStack(Material.EMERALD);
        ItemMeta treasurerMeta = treasurerItem.getItemMeta();

        if (!TownMetaData.hasTreasurer(town)) {

            treasurerMeta.displayName(ColorParser.of("<green>Hire the Treasurer").build());
            treasurerMeta.lore(List.of(
                ColorParser.of("<grey>The Treasurer costs " + Cfg.get().getInt("treasurer.upgrade-cost.level-1") + "⊚ to hire.").build(),
                ColorParser.of("<grey>The Treasurer will allow you to increase your town bank limit.").build()));
        } else {
            if (StewardLookup.get().getSteward(TownMetaData.getTreasurer(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                treasurerMeta.displayName(ColorParser.of("<green>Your Treasurer is striking").build());
                treasurerMeta.lore(List.of(
                    ColorParser.of("<grey>You weren't able to pay your Treasurer's wage, so they are now striking.").build(),
                    ColorParser.of("<grey>Getting your Treasurer back costs <cost>⊚.")
                        .parseMinimessagePlaceholder("cost",
                            String.valueOf(Cfg.get().getInt("treasurer.stipend.level-" +
                                StewardLookup.get().getSteward(TownMetaData.getTreasurer(town)).getLevel())))
                        .build()));
            } else {
                treasurerMeta.displayName(ColorParser.of("<green>Treasurer").build());
                treasurerMeta.lore(List.of(
                    ColorParser.of("<grey>You've already hired the Treasurer.").build(),
                    ColorParser.of("<grey>Talk to the Treasurer to upgrade them and increase your bank limit.").build()));
            }
        }
        treasurerMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        treasurerItem.setItemMeta(treasurerMeta);

        ItemStack bailiffItem = new ItemStack(Material.OAK_DOOR); // TODO translations
        ItemMeta bailiffMeta = bailiffItem.getItemMeta();

        if (!TownMetaData.hasBailiff(town)) {
            bailiffMeta.displayName(ColorParser.of("<green>Hire the Bailiff").build());
            bailiffMeta.lore(List.of(
                ColorParser.of("<grey>The Bailiff costs " + Cfg.get().getInt("bailiff.upgrade-cost.level-1") + "⊚ to hire.").build(),
                ColorParser.of("<grey>The Bailiff will grant you extra claims.").build()));
        } else {
            bailiffMeta.displayName(ColorParser.of("<green>Bailiff").build());
            bailiffMeta.lore(List.of(
                ColorParser.of("<grey>You've already hired the Bailiff.").build(),
                ColorParser.of("<grey>Talk to the Bailiff to upgrade them and increase your bank limit.").build()));
        }
        bailiffMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        bailiffItem.setItemMeta(bailiffMeta);

        ItemStack portmasterItem = new ItemStack(Material.OAK_BOAT);
        ItemMeta portmasterMeta = portmasterItem.getItemMeta();

        if (!TownMetaData.hasPortmaster(town)) {
            portmasterMeta.displayName(ColorParser.of("<green>Hire the Port Master").build());
            portmasterMeta.lore(List.of(
                ColorParser.of("<grey>The Port Master costs " + Cfg.get().getInt("portmaster.upgrade-cost.level-1") + "⊚ to hire.").build(),
                ColorParser.of("<grey>The Port Master will allow you to create a port.").build()));
        } else {
            if (StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                treasurerMeta.displayName(ColorParser.of("<green>Your Port Master is striking").build());
                treasurerMeta.lore(List.of(
                    ColorParser.of("<grey>You weren't able to pay your Port Master's wage, so they are now striking.").build(),
                    ColorParser.of("<grey>Getting your Port Master back costs <cost>⊚.")
                        .parseMinimessagePlaceholder("cost",
                            String.valueOf(Cfg.get().getInt("portmaster.stipend.level-" +
                                StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getLevel())))
                        .build()));
            } else {
                portmasterMeta.displayName(ColorParser.of("<green>Port Master").build());
                portmasterMeta.lore(List.of(
                    ColorParser.of("<grey>You've already hired the Port Master.").build(),
                    ColorParser.of("<grey>Talk to the Port Master to upgrade your port.").build()));
            }
        }
        portmasterMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        portmasterItem.setItemMeta(portmasterMeta);

        ItemStack stablemasterItem = new ItemStack(Material.SADDLE);
        ItemMeta stablemasterMeta = stablemasterItem.getItemMeta();


        if (!TownMetaData.hasStablemaster(town)) {
            stablemasterMeta.displayName(ColorParser.of("<green>Hire the Stable Master").build());
            stablemasterMeta.lore(List.of(
                ColorParser.of("<grey>The Stable Master costs " + Cfg.get().getInt("stablemaster.upgrade-cost.level-1") + "⊚ to hire.").build(),
                ColorParser.of("<grey>The Stable Master will allow you to create a carriage station.").build()));
        } else {
            if (StewardLookup.get().getSteward(TownMetaData.getStablemaster(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                stablemasterMeta.displayName(ColorParser.of("<green>Your Stable Master is striking").build());
                stablemasterMeta.lore(List.of(
                    ColorParser.of("<grey>You weren't able to pay your Stable Master's wage, so they are now striking.").build(),
                    ColorParser.of("<grey>Getting your Stable Master back costs <cost>⊚.")
                        .parseMinimessagePlaceholder("cost",
                            String.valueOf(Cfg.get().getInt("stablemaster.stipend.level-" +
                                StewardLookup.get().getSteward(TownMetaData.getStablemaster(town)).getLevel())))
                        .build()));
            } else {
                stablemasterMeta.displayName(ColorParser.of("<green>Stable Master").build());
                stablemasterMeta.lore(List.of(
                    ColorParser.of("<grey>You've already hired the Stable Master").build(),
                    ColorParser.of("<grey>Talk to the Stable Master to upgrade your carriage station.").build()));
            }
        }
        stablemasterMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        stablemasterItem.setItemMeta(stablemasterMeta);

        gui.setItem(3, 2, ItemBuilder.from(treasurerItem).asGuiItem(e -> {
            if (TownMetaData.hasTreasurer(town)) {
                if (StewardLookup.get().getSteward(TownMetaData.getTreasurer(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                    ConfirmStipendGui.createGui(steward, player, Cfg.get().getInt("treasurer.stipend.level-" +
                            StewardLookup.get().getSteward(TownMetaData.getTreasurer(town)).getLevel()))
                        .open(player);
                }
            } else {
                if (!unhiredSteward) {
                    createSteward(Stewards.getInstance().getStewardTypeHandler().TREASURER_ID, player);
                    TownMetaData.setUnhiredSteward(town, true);
                } else {
                    player.sendMessage(ColorParser.of("<red>You need to hire or dismiss the spawned steward before you can spawn another one.").build());
                }
                gui.close(player);
            }
        }));

        gui.setItem(3, 4, ItemBuilder.from(bailiffItem).asGuiItem(e -> {
            if (TownMetaData.hasBailiff(town)) {
                return;
            } else {
                if (!unhiredSteward) {
                    createSteward(Stewards.getInstance().getStewardTypeHandler().BAILIFF_ID, player);
                    TownMetaData.setUnhiredSteward(town, true);
                } else {
                    player.sendMessage(ColorParser.of("<red>You need to hire or dismiss the spawned steward before you can spawn another one.").build());
                }
            }
            gui.close(player);
        }));

        gui.setItem(3, 6, ItemBuilder.from(portmasterItem).asGuiItem(e -> {
            if (TownMetaData.hasPortmaster(town)) {
                if (StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                    ConfirmStipendGui.createGui(steward, player, Cfg.get().getInt("portmaster.stipend.level-" +
                            StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getLevel()))
                        .open(player);
                }
            } else {
                if (!unhiredSteward) {
                    createSteward(Stewards.getInstance().getStewardTypeHandler().PORTMASTER_ID, player);
                    TownMetaData.setUnhiredSteward(town, true);
                } else {
                    player.sendMessage(ColorParser.of("<red>You need to hire or dismiss the spawned steward before you can spawn another one.").build());
                }
                gui.close(player);
            }
        }));

        gui.setItem(3, 8, ItemBuilder.from(stablemasterItem).asGuiItem(e -> {
            if (TownMetaData.hasStablemaster(town)) {
                if (StewardLookup.get().getSteward(TownMetaData.getStablemaster(town)).getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                    ConfirmStipendGui.createGui(steward, player, Cfg.get().getInt("stablemaster.stipend.level-" +
                            StewardLookup.get().getSteward(TownMetaData.getPortmaster(town)).getLevel()))
                        .open(player);
                }
            } else {
                if (!unhiredSteward) {
                    createSteward(Stewards.getInstance().getStewardTypeHandler().STABLEMASTER_ID, player);
                    TownMetaData.setUnhiredSteward(town, true);
                } else {
                    player.sendMessage(ColorParser.of("<red>You need to hire or dismiss the spawned steward before you can spawn another one.").build());
                }
                gui.close(player);
            }
        }));
    }

    private static void createSteward(String stewardTypeId, Player player) {
        try {
            boolean female = Math.random() > 0.5;
            String name = Appearance.getMaleName();
            if (female)
                name = Appearance.getFemaleName();

            Companion settler = new SettlerBuilder()
                .setName(name)
                .setLocation((player).getLocation().add(Appearance.randomInt(2), 0, Appearance.randomInt(2))) // TODO maybe change this
                .createCompanion();

            Steward steward = Steward.builder()
                .setStewardType(Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(stewardTypeId))
                .setDailyUpkeepCost(0)
                .setIsEnabled(true)
                .setIsHidden(false)
                .setLevel(1)
                .setSettler(settler)
                .setTownUUID(TownyAPI.getInstance().getTown(player).getUUID())
                .build();

            steward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).setTownUUID(TownyAPI.getInstance().getTown(player).getUUID());

            HologramTrait hologramTrait = steward.getSettler().getNpc().getOrAddTrait(HologramTrait.class);
            hologramTrait.addLine(steward.getStewardType().getName());

            if (female) {
                Appearance.applyFemaleStewardSkin(steward);
            } else {
                Appearance.applyMaleStewardSkin(steward);
            }

            if (Objects.equals(stewardTypeId, Stewards.getInstance().getStewardTypeHandler().BAILIFF_ID)) {
                steward.getSettler().getNpc().getOrAddTrait(BailiffTrait.class);
            } else if (Objects.equals(stewardTypeId, Stewards.getInstance().getStewardTypeHandler().PORTMASTER_ID)) {
                steward.getSettler().getNpc().getOrAddTrait(PortmasterTrait.class);
            } else if (Objects.equals(stewardTypeId, Stewards.getInstance().getStewardTypeHandler().STABLEMASTER_ID)) {
                steward.getSettler().getNpc().getOrAddTrait(StablemasterTrait.class);
            } else if (Objects.equals(stewardTypeId, Stewards.getInstance().getStewardTypeHandler().TREASURER_ID)) {
                steward.getSettler().getNpc().getOrAddTrait(TreasurerTrait.class);
            } else {
                throw new InvalidStewardException("Invalid steward type: " + stewardTypeId);
            }

            StewardLookup.get().registerSteward(steward);

            settler.spawn();
        } catch (InvalidStewardException e) {
            throw new RuntimeException(e);
        }
    }
}
