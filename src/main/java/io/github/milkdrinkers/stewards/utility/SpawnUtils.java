package io.github.milkdrinkers.stewards.utility;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import io.github.alathra.activeupkeep.api.ActiveUpkeepAPI;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.settlers.api.settler.SettlerBuilder;
import io.github.milkdrinkers.settlers.api.settler.Townfolk;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.*;
import io.github.milkdrinkers.wordweaver.Translation;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static io.github.milkdrinkers.stewards.steward.StewardTypeHandler.ARCHITECT_ID;

public final class SpawnUtils {
    /**
     * Create a new steward of type for a town.
     *
     * @param type   the steward type
     * @param town   the town
     * @param player the player target for the new steward
     * @return the new steward or null
     * @implNote This method is idempotent.
     */
    public static @Nullable Steward createSteward(StewardType type, Town town, Player player, @Nullable Location location) {
        final StewardType architectType = Objects.requireNonNull(StewardsAPI.getRegistry().getType(ARCHITECT_ID), "Architect type not found in registry");
        final boolean isArchitect = type.equals(architectType);

        if (!isArchitect) {
            final boolean isHiringSteward = TownMetaData.isHiringSteward(town);

            // Town is already hiring a steward
            if (isHiringSteward && player != null) {
                player.sendMessage(ColorParser.of(Translation.of("error.dismiss")).build());
                return null;
            }
        }

        try {
            final boolean female = Math.random() > 0.5;
            final String name = female ? Appearance.getFemaleName() : Appearance.getMaleName();
            final Location spawnLocation = location == null ? player.getLocation().clone().add(Appearance.randomInt(2), 0, Appearance.randomInt(2)) : location;

            final Townfolk settler = new SettlerBuilder()
                .setName(name)
                .setLocation(spawnLocation)
                .createTownfolk();

            final Steward.Builder builder = Steward.builder()
                .setStewardType(type)
                .setDailyUpkeepCost(0)
                .setIsEnabled(true)
                .setIsHidden(false)
                .setLevel(1)
                .setSettler(settler);

            if (!isArchitect)
                builder.setTownUUID(town.getUUID());

            final Steward steward = builder.build();

            final StewardTrait trait = steward.getTrait();
            trait.setFemale(female);
            if (!isArchitect)
                trait.setTownUUID(town.getUUID());

            steward.getNpc().getOrAddTrait(type.trait());
            if (isArchitect) {
                final ArchitectTrait architectTrait = steward.getNpc().getOrAddTrait(ArchitectTrait.class);
                architectTrait.setCreateTime(Instant.now());
                architectTrait.setSpawningPlayer(player.getUniqueId());
            }

            final HologramTrait hologramTrait = steward.getNpc().getOrAddTrait(HologramTrait.class);
            hologramTrait.clear();
            if (isArchitect) {
                hologramTrait.addLine("&7[&6" + steward.getStewardType().name() + "&7]");
            } else {
                hologramTrait.addLine("&7[&b" + steward.getStewardType().name() + "&7]" + " &aLvl " + steward.getLevel());
            }

            final LookClose lookTrait = steward.getNpc().getOrAddTrait(LookClose.class);
            lookTrait.setRange(16);
            lookTrait.lookClose(true);

            if (female) {
                Appearance.applyFemaleStewardSkin(steward);
            } else {
                Appearance.applyMaleStewardSkin(steward);
            }

            StewardsAPI.getLookup().add(steward);
            if (isArchitect)
                StewardsAPI.getLookupArchitect().setArchitect(player, steward);
            if (!isArchitect)
                TownMetaData.setHiringSteward(town, true);

            settler.spawn();
            steward.startFollowing(player);

            return steward;
        } catch (InvalidStewardException e) {
            Logger.get().error("Error while creating steward", e);
            return null;
        }
    }

    public static void hireSteward(Steward steward, Town town, Player player, int cost, boolean checkChunk) {
        final boolean hasCost = cost > 0;
        final StewardTrait trait = steward.getTrait();

        if (town == null) { // This should never happen, as player was allowed to interact with steward
            Logger.get().error("Something went wrong when checking town for {}. Town was null.", player.getName());
            return;
        }

        if (checkChunk && !testStewardLocationCreation(town).test(steward.getNpc().getEntity().getLocation())) {
            player.sendMessage(ColorParser.of(Translation.of("gui.hire.too-close")).build());
            return;
        }

        if (hasCost && !CheckUtils.canAfford(town, cost)) {
            player.sendMessage(ColorParser.of(Translation.of("gui.hire.not-enough-funds")).build());
            return;
        }

        if (checkChunk && !testPortMasterLocation().test(steward)) {
            player.sendMessage(ColorParser.of("<red>The Port Master needs to be closer to water.").build()); // TODO Translate
            return;
        }

        player.sendMessage(ColorParser.of(Translation.of("gui.hire.hire-success")).with("type", steward.getStewardType().name()).build());
        if (hasCost)
            CheckUtils.pay(town, cost, "Stewards: Hired " + steward.getStewardType().name());
        trait.hire();

        steward.setTownUUID(town.getUUID());
        steward.setLevel(1);

        TownMetaData.setHiringSteward(town, false);
        TownMetaData.NPC.set(town, steward);

        if (steward.hasTrait(BailiffTrait.class)) {
            ActiveUpkeepAPI.setBonusClaims(town, ActiveUpkeepAPI.getBonusClaims(town) + Cfg.get().getInt("bailiff.claims.level-1"));
        } else if (steward.hasTrait(PortmasterTrait.class)) {
            PortsAPI.createAbstractPort(TownyAPI.getInstance().getTownName(player), steward.getSettler().getNpc().getEntity().getLocation());
        } else if (steward.hasTrait(StablemasterTrait.class)) {
            PortsAPI.createAbstractCarriageStation(TownyAPI.getInstance().getTownName(player), steward.getSettler().getNpc().getEntity().getLocation());
        } else if (steward.hasTrait(TreasurerTrait.class)) {
            TownMetaData.setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-1"));
        } else { // This should never happen.
            Logger.get().error("Something went wrong: No type-specific trait was found for {}", steward.getSettler().getNpc().getId());
        }

        if (trait.isFollowing())
            steward.stopFollowing(trait.getFollowingPlayer(), true);

        StewardsAPI.getLookupTown().add(town, steward);
    }

    /**
     *
     * @param location the location to check
     * @param town the town this steward already belongs to
     * @param movingSteward the steward being moved (if any)
     * @return true if town block can be occupied by a steward
     */
    private static boolean isValidTownBlock(Location location, @Nullable Town town, @Nullable Steward movingSteward) {
        Objects.requireNonNull(location, "Location is null");
        if (town == null)
            return false;

        final Chunk chunk = location.getChunk();

        // Prevent steward in same chunk
        for (StewardType type : StewardsAPI.getRegistry()) {
            if (TownMetaData.NPC.has(town, type) && TownMetaData.NPC.getStewardOptional(town, type).isPresent()) {
                final Steward otherSteward = TownMetaData.NPC.getStewardOptional(town, type).get();

                // Skip check if the steward being moved is the same
                if (movingSteward != null && movingSteward.equals(otherSteward))
                    continue;

                final Chunk otherChunk = otherSteward.getTrait().getAnchorLocation().getChunk();

                if (otherSteward.getNpc().isSpawned() && otherChunk.isLoaded() && otherChunk.getChunkKey() == chunk.getChunkKey()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Test if this steward can be placed/moved in this chunk.
     *
     * @return if not portmaster or if portmaster and in ocean
     */
    public static Predicate<Steward> testStewardLocationMove() {
        return steward -> {
            if (steward.isFounder()) // Ignore location check for unhired steward
                return true;

            final Town town = TownyAPI.getInstance().getTown(steward.getTownUUID());
            if (town == null)
                return true;

            return isValidTownBlock(steward.getNpc().getEntity().getLocation(), town, steward);
        };
    }

    /**
     * Test if this steward can be placed in this chunk.
     *
     * @return if not valid location
     */
    public static Predicate<Location> testStewardLocationCreation(@Nullable Town town) {
        return location -> isValidTownBlock(location, town, null);
    }

    /**
     * Test if this steward is in an ocean chunk if its a portmaster.
     *
     * @return if not portmaster or if portmaster and in ocean
     */
    public static Predicate<Steward> testPortMasterLocation() {
        return steward -> {
            if (!steward.hasTrait(PortmasterTrait.class))
                return true;

            return isOceanChunk(steward);
        };
    }

    /**
     * Whether the chunk should be considered a water chunk and allow a portmaster in it
     *
     * @param steward the steward
     * @return true if water chunk
     */
    private static boolean isOceanChunk(Steward steward) {
        final Chunk chunk = steward.getSettler().getNpc().getEntity().getChunk();

        final List<String> biomeList = Cfg.get().getStringList("portmaster.allowed-biomes");
//        final Registry<@NotNull Biome> registry =  RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
//        final Set<Biome> biomes = biomeList.stream()
//            .map(NamespacedKey::fromString)
//            .filter(Objects::nonNull)
//            .map(registry::get)
//            .collect(Collectors.toSet());
//
//        for (Biome biome : biomes) {
//            if (chunk.contains(biome))
//                return true;
//        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (biomeList.contains(chunk.getBlock(x, 64, z).getBiome().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Location getSuitableLosLocation(final Player player) {
        final List<Block> lineOfSight = player.getLineOfSight(null, 50);

        for (final Block block : lineOfSight) {
            final Location loc = block.getLocation();
            if (canFitSteward(loc))
                return loc.clone().add(0.5, 1, 0.5);
        }

        return null;
    }

    private static boolean canFitSteward(final Location loc) {
        final World world = loc.getWorld();
        if (world == null)
            return false;

        final Block groundBlock = world.getBlockAt(loc);
        final Block legBlock = world.getBlockAt(loc.clone().add(0, 1, 0));
        final Block headBlock = world.getBlockAt(loc.clone().add(0, 2, 0));

        if (!groundBlock.getType().isSolid())
            return false;

        if (!legBlock.getType().isAir() && !legBlock.isPassable())
            return false;

        return headBlock.getType().isAir() || headBlock.isPassable();
    }
}
