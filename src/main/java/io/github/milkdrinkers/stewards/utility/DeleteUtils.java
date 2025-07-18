package io.github.milkdrinkers.stewards.utility;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.github.alathra.alathraports.api.PortsAPI;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.towny.TownMetaData;
import io.github.milkdrinkers.stewards.trait.*;
import io.github.milkdrinkers.wordweaver.Translation;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

import static io.github.milkdrinkers.stewards.steward.StewardTypeHandler.ARCHITECT_ID;

public final class DeleteUtils {
    public static void dismiss(Steward steward, @Nullable Town town, @Nullable Player player, boolean message) {
        dismiss(steward, town != null ? town.getUUID() : null, player, message);
    }

    public static void dismiss(Steward steward, @Nullable UUID townUUID, @Nullable Player player, boolean message) {
        Objects.requireNonNull(steward, "Steward cannot be null");

        final StewardType architectType = Objects.requireNonNull(StewardsAPI.getRegistry().getType(ARCHITECT_ID), "Architect type not found in registry");
        final boolean isArchitect = steward.getStewardType().equals(architectType);

        final StewardTrait trait = steward.getTrait();
        if (trait.isFollowing())
            steward.stopFollowing(trait.getFollowingPlayer(), false);

        final Town town = townUUID != null ? TownyAPI.getInstance().getTown(townUUID) : null;

        if (trait.isHired()) {
            if (steward.getNpc().hasTrait(BailiffTrait.class) && town != null) {
                town.addBonusBlocks(-1 * Cfg.get().getInt("bailiff.claims.level-" + steward.getLevel()));
            } else if (steward.getNpc().hasTrait(PortmasterTrait.class)) {
                PortsAPI.deleteAbstractPort(PortsAPI.getPortFromTownUUID(townUUID));
            } else if (steward.getNpc().hasTrait(StablemasterTrait.class)) {
                PortsAPI.deleteAbstractCarriageStation(PortsAPI.getCarriageStationFromTownUUID(townUUID));
            } else if (steward.getNpc().hasTrait(TreasurerTrait.class) && town != null) {
                TownMetaData.setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-0"));
            }
        } else {
            // If the steward is not hired then clear town key
            if (player != null) {
                final Resident resident = TownyAPI.getInstance().getResident(player);
                if (resident != null && resident.hasTown())
                    TownMetaData.setHiringSteward(resident.getTownOrNull(), false);
            }
        }

        if (player != null && message)
            player.sendMessage(ColorParser.of(Translation.of("gui.fire.fire-success")).with("type", steward.getStewardType().name()).build().decoration(TextDecoration.ITALIC, false));

        if (townUUID != null) {
            if (town != null)
                TownMetaData.NPC.remove(town, steward);
            StewardsAPI.getLookupTown().remove(townUUID, steward.getUniqueId());
        }
        if (isArchitect && player != null)
            StewardsAPI.getLookupArchitect().clearHasArchitect(player);
        StewardsAPI.getLookup().remove(steward);
        steward.getSettler().delete();
    }
}
