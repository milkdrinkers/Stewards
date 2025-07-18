package io.github.milkdrinkers.stewards.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.conversation.CreateTownConversation;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.ArchitectTrait;
import io.github.milkdrinkers.stewards.trait.BailiffTrait;
import io.github.milkdrinkers.stewards.trait.StewardTrait;
import io.github.milkdrinkers.stewards.utility.Cfg;
import io.github.milkdrinkers.stewards.utility.SpawnUtils;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

public class StewardBaseGui { // TODO refactor this absolutely disgusting class

    public static Gui createBaseGui(Steward steward, Player player) {
        Stewards plugin = Stewards.getInstance();

        Gui gui = Gui.gui()
            .title(ColorParser.of(steward.getStewardType().settlerPrefix()
                + " " + steward.getNpc().getName()).build())
            .rows(5).create();

        gui.disableItemDrop()
            .disableItemPlace()
            .disableItemSwap()
            .disableItemTake();

        populateBorders(gui);
        populateButtons(gui, steward, player);

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(StewardTypeHandler.ARCHITECT_ID)) {
            if (steward.getTrait().isHired()) {
                populateArchitectTownButtons(gui, steward, player);
            }else {
                populateArchitectNoTownButtons(gui, steward, player);
            }
        }

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(StewardTypeHandler.TREASURER_ID)) {
            if (steward.getTrait().isHired()) {
                populateHiredButtons(gui, steward, player);
            } else {
                populateUnHiredButtons(gui, steward, player);
            }
        }

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(StewardTypeHandler.BAILIFF_ID)) {
            if (steward.getTrait().isHired()) {
                populateHiredButtons(gui, steward, player);
            } else {
                populateUnHiredButtons(gui, steward, player);
            }
        }

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(StewardTypeHandler.PORTMASTER_ID)) {
            if (steward.getTrait().isHired()) {
                populateHiredButtons(gui, steward, player);
            } else {
                populateUnHiredButtons(gui, steward, player);
            }
        }

        if (steward.getStewardType() == plugin.getStewardTypeHandler().getStewardTypeRegistry().getType(StewardTypeHandler.STABLEMASTER_ID)) {
            if (steward.getTrait().isHired()) {
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
        exitMeta.displayName(ColorParser.of(Translation.of("gui.general.exit")).build().decoration(TextDecoration.ITALIC, false));
        exitMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        exitItem.setItemMeta(exitMeta);

        gui.setItem(5, 9, ItemBuilder.from(exitItem).asGuiItem(event -> gui.close(player)));

        ItemStack appearanceItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta appearanceMeta = appearanceItem.getItemMeta();
        appearanceMeta.displayName(ColorParser.of(Translation.of("gui.general.re-roll")).build().decoration(TextDecoration.ITALIC, false));
        appearanceMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        appearanceItem.setItemMeta(appearanceMeta);

        gui.setItem(5, 1, ItemBuilder.from(appearanceItem).asGuiItem(event -> AppearanceGui.createGui(steward, player).open(player)));


        ItemStack followItem = new ItemStack(Material.LEAD);
        ItemMeta followMeta = followItem.getItemMeta();


        if (steward.getTrait().isFollowing()) {
            followMeta.displayName(ColorParser.of(Translation.of("gui.general.stop-following")).build().decoration(TextDecoration.ITALIC, false));
        } else {
            followMeta.displayName(ColorParser.of(Translation.of("gui.general.follow")).build().decoration(TextDecoration.ITALIC, false));
        }

        followMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        followItem.setItemMeta(followMeta);

        gui.setItem(5, 5, ItemBuilder.from(followItem).asGuiItem(event -> {
            StewardTrait trait = steward.getTrait();
            if (trait == null) return;

            if (trait.isFollowing()) {
                ConfirmMoveGui.createGui(steward, player).open(player);
            } else {
                steward.startFollowing(player);
                gui.close(player);
            }
        }));

        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.displayName(ColorParser.of(Translation.of("gui.general.info.name")).with("name", steward.getNpc().getName()).build().decoration(TextDecoration.ITALIC, false));
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

        infoMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        infoItem.setItemMeta(infoMeta);

        gui.setItem(1, 5, ItemBuilder.from(infoItem).asGuiItem());
    }

    private static void populateUnHiredButtons(Gui gui, Steward steward, Player player) {
        int cost = Cfg.get().getInt(steward.getStewardType().name().toLowerCase().replace(" ", "") + ".upgrade-cost.level-1");

        ItemStack hireItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta hireMeta = hireItem.getItemMeta();
        hireMeta.displayName(ColorParser.of(Translation.of("gui.general.hire.name")).build().decoration(TextDecoration.ITALIC, false));
        hireMeta.lore(List.of(ColorParser.of(Translation.of("gui.general.hire.cost"))
            .with("cost", String.valueOf(cost)).build().decoration(TextDecoration.ITALIC, false)));
        hireMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        hireItem.setItemMeta(hireMeta);

        ItemStack dismissItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta dismissMeta = dismissItem.getItemMeta();
        dismissMeta.displayName(ColorParser.of(Translation.of("gui.general.dismiss.name")).build().decoration(TextDecoration.ITALIC, false));
        dismissMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        dismissItem.setItemMeta(dismissMeta);

        gui.setItem(3, 3, ItemBuilder.from(hireItem).asGuiItem(event -> {
            ConfirmHireGui.createGui(steward, player, cost).open(player);
        }));

        gui.setItem(3, 7, ItemBuilder.from(dismissItem).asGuiItem(event -> {
            ConfirmDismissGui.createGui(steward, player).open(player);
        }));
    }

    private static void populateHiredButtons(Gui gui, Steward steward, Player player) {
        int cost = Cfg.get().getInt(steward.getStewardType().name().toLowerCase()
            .replace(" ", "") + ".upgrade-cost.level-" + (steward.getLevel() + 1));

        ItemStack upgradeItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta upgradeMeta = upgradeItem.getItemMeta();

        if (steward.getLevel() < steward.getStewardType().maxLevel()) {
            upgradeMeta.displayName(ColorParser.of(Translation.of("gui.general.upgrade.name-1"))
                .with("type", steward.getStewardType().name()).build().decoration(TextDecoration.ITALIC, false));
            upgradeMeta.lore(List.of(ColorParser.of(Translation.of("gui.general.upgrade.lore-1"))
                .with("cost", String.valueOf(cost)).build().decoration(TextDecoration.ITALIC, false)));
        } else {
            upgradeMeta.displayName(ColorParser.of(Translation.of("gui.general.upgrade.name-2")).with("type", steward.getStewardType().name()).build().decoration(TextDecoration.ITALIC, false));
            upgradeMeta.lore(List.of(ColorParser.of(Translation.of("gui.general.upgrade.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
        }

        upgradeMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        upgradeItem.setItemMeta(upgradeMeta);

        ItemStack fireItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta fireMeta = fireItem.getItemMeta();
        fireMeta.displayName(ColorParser.of(Translation.of("gui.general.fire.name")).build().decoration(TextDecoration.ITALIC, false));
        fireMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        fireItem.setItemMeta(fireMeta);

        if (steward.getStewardType() == Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(StewardTypeHandler.PORTMASTER_ID)) {

            if (TownyAPI.getInstance().getResident(player).isMayor()) {
                gui.setItem(3, 3, ItemBuilder.from(upgradeItem).asGuiItem(event -> {
                    if (steward.getLevel() < steward.getStewardType().maxLevel()) {
                        ConfirmUpgradeGui.createGui(steward, player, cost).open(player);
                    } else {
                        gui.close(player);
                        player.sendMessage(ColorParser.of(Translation.of("gui.general.upgrade.unclickable-message")).build().decoration(TextDecoration.ITALIC, false));
                    }
                }));

                gui.setItem(3, 7, ItemBuilder.from(fireItem).asGuiItem(event -> {
                    ConfirmFireGui.createGui(steward, player).open(player);
                }));
            }

            ItemStack portItem = new ItemStack(Material.OAK_BOAT);
            ItemMeta portMeta = portItem.getItemMeta();
            portMeta.displayName(ColorParser.of(Translation.of("gui.general.travel.name")).build().decoration(TextDecoration.ITALIC, false));
            portMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            portItem.setItemMeta(portMeta);
            gui.setItem(3, 5, ItemBuilder.from(portItem).asGuiItem(event -> {
                PortsAPI.openTravelMenu(player, PortsAPI.getPortFromTown(TownyAPI.getInstance().getTown(steward.getTownUUID())));
            }));

        } else if (steward.getStewardType() == Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().getType(StewardTypeHandler.STABLEMASTER_ID)) {

            if (TownyAPI.getInstance().getResident(player).isMayor()) {
                gui.setItem(3, 3, ItemBuilder.from(upgradeItem).asGuiItem(event -> {
                    if (steward.getLevel() < steward.getStewardType().maxLevel()) {
                        ConfirmUpgradeGui.createGui(steward, player, cost).open(player);
                    } else {
                        gui.close(player);
                        player.sendMessage(ColorParser.of(Translation.of("gui.general.upgrade.unclickable-message")).build().decoration(TextDecoration.ITALIC, false));
                    }
                }));

                gui.setItem(3, 7, ItemBuilder.from(fireItem).asGuiItem(event -> {
                    ConfirmFireGui.createGui(steward, player).open(player);
                }));
            }

            ItemStack stationItem = new ItemStack(Material.SADDLE);
            ItemMeta stationMeta = stationItem.getItemMeta();
            stationMeta.displayName(ColorParser.of(Translation.of("gui.general.travel.name")).build().decoration(TextDecoration.ITALIC, false));
            stationMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            stationItem.setItemMeta(stationMeta);

            gui.setItem(3, 5, ItemBuilder.from(stationItem).asGuiItem(event -> {
                PortsAPI.openTravelMenu(player, PortsAPI.getCarriageStationFromTown(TownyAPI.getInstance().getTown(steward.getTownUUID())));
            }));

        } else {
            gui.setItem(3, 3, ItemBuilder.from(upgradeItem).asGuiItem(event -> {
                if (steward.getLevel() < steward.getStewardType().maxLevel()) {
                    ConfirmUpgradeGui.createGui(steward, player, cost).open(player);
                } else {
                    gui.close(player);
                    player.sendMessage(ColorParser.of(Translation.of("gui.general.upgrade.unclickable-message")).build());
                }
            }));

            gui.setItem(3, 7, ItemBuilder.from(fireItem).asGuiItem(event -> {
                ConfirmFireGui.createGui(steward, player).open(player);
            }));
        }
    }

    private static void populateArchitectNoTownButtons(Gui gui, Steward steward, Player player) {
        ItemStack townItem = new ItemStack(Material.RED_BED);
        ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(ColorParser.of(Translation.of("gui.architect.create.name")).build().decoration(TextDecoration.ITALIC, false));
        townMeta.lore(List.of(ColorParser.of(Translation.of("gui.architect.create.lore")).with("cost", String.valueOf(Math.round(TownySettings.getNewTownPrice()))).build().decoration(TextDecoration.ITALIC, false)));
        townMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        townItem.setItemMeta(townMeta);

        ItemStack dismissItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta dismissMeta = dismissItem.getItemMeta();
        dismissMeta.displayName(ColorParser.of(Translation.of("gui.architect.dismiss.name")).build().decoration(TextDecoration.ITALIC, false));
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
        Town town = TownyAPI.getInstance().getTown(player);

        final StewardType treasurerType = StewardsAPI.getRegistry().getType(StewardTypeHandler.TREASURER_ID);
        final StewardType bailiffType = StewardsAPI.getRegistry().getType(StewardTypeHandler.BAILIFF_ID);
        final StewardType portType = StewardsAPI.getRegistry().getType(StewardTypeHandler.PORTMASTER_ID);
        final StewardType stableType = StewardsAPI.getRegistry().getType(StewardTypeHandler.STABLEMASTER_ID);
        if (treasurerType == null || bailiffType == null || portType == null || stableType == null)
            throw new IllegalStateException("Steward registry type was null");

        ItemStack treasurerItem = new ItemStack(Material.EMERALD);
        ItemMeta treasurerMeta = treasurerItem.getItemMeta();

        if (!TownMetaData.NPC.has(town, treasurerType)) {
            treasurerMeta.displayName(ColorParser.of(Translation.of("gui.architect.treasurer.hire.name")).build().decoration(TextDecoration.ITALIC, false));
            treasurerMeta.lore(List.of(
                ColorParser.of(Translation.of("gui.architect.treasurer.hire.lore-1")).with("cost", String.valueOf(Cfg.get().getInt("treasurer.upgrade-cost.level-1"))).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of(Translation.of("gui.architect.treasurer.hire.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
        } else {
            final Optional<Steward> stewardOptional = TownMetaData.NPC.getStewardOptional(town, treasurerType);

            if (stewardOptional.isPresent() && stewardOptional.get().getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                treasurerMeta.displayName(ColorParser.of(Translation.of("gui.architect.treasurer.striking.name")).build().decoration(TextDecoration.ITALIC, false));
                treasurerMeta.lore(List.of(
                    ColorParser.of(Translation.of("gui.architect.treasurer.striking.lore-1")).build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of(Translation.of("gui.architect.treasurer.striking.lore-2"))
                        .with("cost",
                            String.valueOf(Cfg.get().getInt("treasurer.stipend.level-" +
                                stewardOptional.get().getLevel())))
                        .build().decoration(TextDecoration.ITALIC, false)));
            } else {
                treasurerMeta.displayName(ColorParser.of(Translation.of("gui.architect.treasurer.hired.name")).build().decoration(TextDecoration.ITALIC, false));
                treasurerMeta.lore(List.of(
                    ColorParser.of(Translation.of("gui.architect.treasurer.hired.lore-1")).build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of(Translation.of("gui.architect.treasurer.hired.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
            }
        }
        treasurerMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        treasurerItem.setItemMeta(treasurerMeta);

        ItemStack bailiffItem = new ItemStack(Material.OAK_DOOR);
        ItemMeta bailiffMeta = bailiffItem.getItemMeta();

        if (!TownMetaData.NPC.has(town, bailiffType)) {
            bailiffMeta.displayName(ColorParser.of(Translation.of("gui.architect.bailiff.hire.name")).build().decoration(TextDecoration.ITALIC, false));
            bailiffMeta.lore(List.of(
                ColorParser.of(Translation.of("gui.architect.bailiff.hire.lore-1")).with("cost", String.valueOf(Cfg.get().getInt("bailiff.upgrade-cost.level-1"))).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of(Translation.of("gui.architect.bailiff.hire.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
        } else {
            bailiffMeta.displayName(ColorParser.of(Translation.of("gui.architect.bailiff.hired.name")).build().decoration(TextDecoration.ITALIC, false));
            bailiffMeta.lore(List.of(
                ColorParser.of(Translation.of("gui.architect.bailiff.hired.lore-1")).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of(Translation.of("gui.architect.bailiff.hired.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
        }
        bailiffMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        bailiffItem.setItemMeta(bailiffMeta);

        ItemStack portmasterItem = new ItemStack(Material.OAK_BOAT);
        ItemMeta portmasterMeta = portmasterItem.getItemMeta();

        if (!TownMetaData.NPC.has(town, portType)) {
            portmasterMeta.displayName(ColorParser.of(Translation.of("gui.architect.portmaster.hire.name")).build().decoration(TextDecoration.ITALIC, false));
            portmasterMeta.lore(List.of(
                ColorParser.of(Translation.of("gui.architect.portmaster.hire.lore-1")).with("cost", String.valueOf(Cfg.get().getInt("portmaster.upgrade-cost.level-1"))).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of(Translation.of("gui.architect.portmaster.hire.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
        } else {
            final Optional<Steward> stewardOptional = TownMetaData.NPC.getStewardOptional(town, portType);

            if (stewardOptional.isPresent() && stewardOptional.get().getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                treasurerMeta.displayName(ColorParser.of(Translation.of("gui.architect.portmaster.striking.name")).build().decoration(TextDecoration.ITALIC, false));
                treasurerMeta.lore(List.of(
                    ColorParser.of(Translation.of("gui.architect.portmaster.striking.lore-1")).build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of(Translation.of("gui.architect.portmaster.striking.lore-2"))
                        .with("cost",
                            String.valueOf(Cfg.get().getInt("portmaster.stipend.level-" +
                                stewardOptional.get().getLevel())))
                        .build().decoration(TextDecoration.ITALIC, false)));
            } else {
                portmasterMeta.displayName(ColorParser.of(Translation.of("gui.architect.portmaster.hired.name")).build().decoration(TextDecoration.ITALIC, false));
                portmasterMeta.lore(List.of(
                    ColorParser.of(Translation.of("gui.architect.portmaster.hired.lore-1")).build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of(Translation.of("gui.architect.portmaster.hired.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
            }
        }
        portmasterMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        portmasterItem.setItemMeta(portmasterMeta);

        ItemStack stablemasterItem = new ItemStack(Material.SADDLE);
        ItemMeta stablemasterMeta = stablemasterItem.getItemMeta();

        if (!TownMetaData.NPC.has(town, stableType)) {
            stablemasterMeta.displayName(ColorParser.of(Translation.of("gui.architect.stablemaster.hire.name")).build().decoration(TextDecoration.ITALIC, false));
            stablemasterMeta.lore(List.of(
                ColorParser.of(Translation.of("gui.architect.stablemaster.hire.lore-1")).with("cost", String.valueOf(Cfg.get().getInt("stablemaster.upgrade-cost.level-1"))).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of(Translation.of("gui.architect.stablemaster.hire.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
        } else {
            final Optional<Steward> stewardOptional = TownMetaData.NPC.getStewardOptional(town, stableType);

            if (stewardOptional.isPresent() && stewardOptional.get().getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                stablemasterMeta.displayName(ColorParser.of(Translation.of("gui.architect.stablemaster.striking.name")).build().decoration(TextDecoration.ITALIC, false));
                stablemasterMeta.lore(List.of(
                    ColorParser.of(Translation.of("gui.architect.stablemaster.hire.lore-1")).build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of(Translation.of("gui.architect.stablemaster.hire.lore-2"))
                        .with("cost",
                            String.valueOf(Cfg.get().getInt("stablemaster.stipend.level-" +
                                stewardOptional.get().getLevel())))
                        .build().decoration(TextDecoration.ITALIC, false)));
            } else {
                stablemasterMeta.displayName(ColorParser.of(Translation.of("gui.architect.stablemaster.hired.name")).build().decoration(TextDecoration.ITALIC, false));
                stablemasterMeta.lore(List.of(
                    ColorParser.of(Translation.of("gui.architect.stablemaster.hired.lore-1")).build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of(Translation.of("gui.architect.stablemaster.hired.lore-2")).build().decoration(TextDecoration.ITALIC, false)));
            }
        }
        stablemasterMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        stablemasterItem.setItemMeta(stablemasterMeta);

        gui.setItem(3, 2, ItemBuilder.from(treasurerItem).asGuiItem(e -> {
            final StewardType type = StewardsAPI.getRegistry().getType(StewardTypeHandler.TREASURER_ID);
            final Optional<Steward> stewardOptional = TownMetaData.NPC.getStewardOptional(town, type);

            if (stewardOptional.isPresent()) {
                final Steward localSteward = stewardOptional.get();

                if (localSteward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                    ConfirmStipendGui.createGui(steward, player, Cfg.get().getInt("treasurer.stipend.level-" +
                            localSteward.getLevel()))
                        .open(player);
                }
            } else {
                SpawnUtils.createSteward(type, town, player, null);
                gui.close(player);
            }
        }));

        gui.setItem(3, 4, ItemBuilder.from(bailiffItem).asGuiItem(e -> {
            final StewardType type = StewardsAPI.getRegistry().getType(StewardTypeHandler.BAILIFF_ID);
            final Optional<Steward> stewardOptional = TownMetaData.NPC.getStewardOptional(town, type);

            if (stewardOptional.isPresent()) {
                final Steward localSteward = stewardOptional.get();

                return;
            } else {
                SpawnUtils.createSteward(type, town, player, null);
            }
            gui.close(player);
        }));

        gui.setItem(3, 6, ItemBuilder.from(portmasterItem).asGuiItem(e -> {
            final StewardType type = StewardsAPI.getRegistry().getType(StewardTypeHandler.PORTMASTER_ID);
            final Optional<Steward> stewardOptional = TownMetaData.NPC.getStewardOptional(town, type);

            if (stewardOptional.isPresent()) {
                final Steward localSteward = stewardOptional.get();

                if (localSteward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                    ConfirmStipendGui.createGui(steward, player, Cfg.get().getInt("portmaster.stipend.level-" +
                            localSteward.getLevel()))
                        .open(player);
                }
            } else {
                SpawnUtils.createSteward(type, town, player, null);
                gui.close(player);
            }
        }));

        gui.setItem(3, 8, ItemBuilder.from(stablemasterItem).asGuiItem(e -> {
            final StewardType type = StewardsAPI.getRegistry().getType(StewardTypeHandler.STABLEMASTER_ID);
            final Optional<Steward> stewardOptional = TownMetaData.NPC.getStewardOptional(town, type);

            if (stewardOptional.isPresent()) {
                final Steward localSteward = stewardOptional.get();

                if (localSteward.getSettler().getNpc().getOrAddTrait(StewardTrait.class).isStriking()) {
                    ConfirmStipendGui.createGui(steward, player, Cfg.get().getInt("stablemaster.stipend.level-" +
                            localSteward.getLevel()))
                        .open(player);
                }
            } else {
                SpawnUtils.createSteward(type, town, player, null);
                gui.close(player);
            }
        }));
    }
}
