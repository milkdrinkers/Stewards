package io.github.milkdrinkers.stewards.towny;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;
import io.github.milkdrinkers.settlers.api.settler.AbstractSettler;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.utility.Cfg;
import net.citizensnpcs.api.npc.NPC;

import javax.annotation.Nullable;
import java.util.UUID;

public class TownMetaData {

    private static final String bankLimit = "stewards_bank_limit";
    private static final String unhiredSteward = "stewards_unhired_steward";
    private static final String architect = "stewards_architect";
    private static final String bailiff = "stewards_bailiff";
    private static final String portmaster = "stewards_portmaster";
    private static final String stablemaster = "stewards_stablemaster";
    private static final String treasurer = "stewards_treasurer";
    private static final String guardcaptain = "stewards_guardcaptain";

    private static final IntegerDataField bankLimitField = new IntegerDataField(bankLimit);

    private static final BooleanDataField unhiredStewardField = new BooleanDataField(unhiredSteward);

    private static final StringDataField architectField = new StringDataField(architect);
    private static final StringDataField bailiffField = new StringDataField(bailiff);
    private static final StringDataField portmasterField = new StringDataField(portmaster);
    private static final StringDataField stablemasterField = new StringDataField(stablemaster);
    private static final StringDataField treasurerField = new StringDataField(treasurer);
    private static final StringDataField guardcaptainField = new StringDataField(guardcaptain);

    public static void removeBailiff(Town town) {
        MetaDataUtil.setString(town, bailiffField, null, true);
    }

    public static void removePortmaster(Town town) {
        MetaDataUtil.setString(town, portmasterField, null, true);
    }

    public static void removeStablemaster(Town town) {
        MetaDataUtil.setString(town, stablemasterField, null, true);
    }

    public static void removeTreasurer(Town town) {
        MetaDataUtil.setString(town, treasurerField, null, true);
    }

    public static void removeGuardcaptain(Town town) {
        MetaDataUtil.setString(town, guardcaptainField, null, true);
    }

    public static boolean hasArchitect(Town town) {
        return MetaDataUtil.getString(town, architectField) != null && !MetaDataUtil.getString(town, architectField).isEmpty();
    }

    public static boolean hasBailiff(Town town) {
        return MetaDataUtil.getString(town, bailiffField) != null && !MetaDataUtil.getString(town, bailiffField).isEmpty();
    }

    public static boolean hasPortmaster(Town town) {
        return MetaDataUtil.getString(town, portmasterField) != null && !MetaDataUtil.getString(town, portmasterField).isEmpty();
    }

    public static boolean hasStablemaster(Town town) {
        return MetaDataUtil.getString(town, stablemasterField) != null && !MetaDataUtil.getString(town, stablemasterField).isEmpty();
    }

    public static boolean hasTreasurer(Town town) {
        return MetaDataUtil.getString(town, treasurerField) != null && !MetaDataUtil.getString(town, treasurerField).isEmpty();
    }

    public static boolean hasGuardcaptain(Town town) {
        return MetaDataUtil.getString(town, guardcaptainField) != null && !MetaDataUtil.getString(town, guardcaptainField).isEmpty();
    }

    public static void setArchitect(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, architect))
            MetaDataUtil.addNewStringMeta(town, architect, "", true);
        MetaDataUtil.setString(town, architectField, uuid.toString(), true);
    }

    public static void setArchitect(Town town, NPC npc) {
        setArchitect(town, npc.getUniqueId());
    }

    public static void setArchitect(Town town, AbstractSettler settler) {
        setArchitect(town, settler.getNpc());
    }

    public static void setArchitect(Town town, Steward steward) {
        setArchitect(town, steward.getSettler());
    }

    public static void setBailiff(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, bailiff))
            MetaDataUtil.addNewStringMeta(town, bailiff, "", true);
        MetaDataUtil.setString(town, bailiffField, uuid.toString(), true);
    }

    public static void setBailiff(Town town, NPC npc) {
        setBailiff(town, npc.getUniqueId());
    }

    public static void setBailiff(Town town, AbstractSettler settler) {
        setBailiff(town, settler.getNpc());
    }

    public static void setBailiff(Town town, Steward steward) {
        setBailiff(town, steward.getSettler());
    }

    public static void setPortmaster(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, portmaster))
            MetaDataUtil.addNewStringMeta(town, portmaster, "", true);
        MetaDataUtil.setString(town, portmasterField, uuid.toString(), true);
    }

    public static void setPortmaster(Town town, NPC npc) {
        setPortmaster(town, npc.getUniqueId());
    }

    public static void setPortmaster(Town town, AbstractSettler settler) {
        setPortmaster(town, settler.getNpc());
    }

    public static void setPortmaster(Town town, Steward steward) {
        setPortmaster(town, steward.getSettler());
    }

    public static void setStablemaster(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, stablemaster))
            MetaDataUtil.addNewStringMeta(town, stablemaster, "", true);
        MetaDataUtil.setString(town, stablemasterField, uuid.toString(), true);
    }

    public static void setStablemaster(Town town, NPC npc) {
        setStablemaster(town, npc.getUniqueId());
    }

    public static void setStablemaster(Town town, AbstractSettler settler) {
        setStablemaster(town, settler.getNpc());
    }

    public static void setStablemaster(Town town, Steward steward) {
        setStablemaster(town, steward.getSettler());
    }

    public static void setTreasurer(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, treasurer))
            MetaDataUtil.addNewStringMeta(town, treasurer, "", true);
        MetaDataUtil.setString(town, treasurerField, uuid.toString(), true);
    }

    public static void setTreasurer(Town town, NPC npc) {
        setTreasurer(town, npc.getUniqueId());
    }

    public static void setTreasurer(Town town, AbstractSettler settler) {
        setTreasurer(town, settler.getNpc());
    }

    public static void setTreasurer(Town town, Steward steward) {
        setTreasurer(town, steward.getSettler());
    }

    public static void setGuardcaptain(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, guardcaptain))
            MetaDataUtil.addNewStringMeta(town, guardcaptain, "", true);
        MetaDataUtil.setString(town, guardcaptainField, uuid.toString(), true);
    }

    public static void setGuardcaptain(Town town, NPC npc) {
        setGuardcaptain(town, npc.getUniqueId());
    }

    public static void setGuardcaptain(Town town, AbstractSettler settler) {
        setGuardcaptain(town, settler.getNpc());
    }

    public static void setGuardcaptain(Town town, Steward steward) {
        setGuardcaptain(town, steward.getSettler());
    }

    public static @Nullable UUID getArchitect(Town town) {
        if (!MetaDataUtil.hasMeta(town, architect))
            return null;
        return UUID.fromString(MetaDataUtil.getString(town, architectField));
    }

    public static @Nullable UUID getBailiff(Town town) {
        if (!MetaDataUtil.hasMeta(town, bailiff))
            return null;
        return UUID.fromString(MetaDataUtil.getString(town, bailiffField));
    }

    public static @Nullable UUID getPortmaster(Town town) {
        if (!MetaDataUtil.hasMeta(town, portmaster))
            return null;
        return UUID.fromString(MetaDataUtil.getString(town, portmasterField));
    }

    public static @Nullable UUID getStablemaster(Town town) {
        if (!MetaDataUtil.hasMeta(town, stablemaster))
            return null;
        return UUID.fromString(MetaDataUtil.getString(town, stablemasterField));
    }

    public static @Nullable UUID getTreasurer(Town town) {
        if (!MetaDataUtil.hasMeta(town, treasurer))
            return null;
        return UUID.fromString(MetaDataUtil.getString(town, treasurerField));
    }

    public static @Nullable UUID getGuardcaptain(Town town) {
        if (!MetaDataUtil.hasMeta(town, guardcaptain))
            return null;
        return UUID.fromString(MetaDataUtil.getString(town, guardcaptainField));
    }

    public static boolean hasUnhiredSteward(Town town) {
        if (!MetaDataUtil.hasMeta(town, unhiredSteward))
            MetaDataUtil.addNewBooleanMeta(town, unhiredSteward, false, true);
        return MetaDataUtil.getBoolean(town, unhiredStewardField);
    }

    public static void setUnhiredSteward(Town town, boolean value) {
        if (!MetaDataUtil.hasMeta(town, unhiredSteward))
            MetaDataUtil.addNewBooleanMeta(town, unhiredSteward, false, true);
        MetaDataUtil.setBoolean(town, unhiredStewardField, value, true);
    }

    public static int getBankLimit(Town town) {
        if (!MetaDataUtil.hasMeta(town, bankLimit)) {
            if (hasTreasurer(town)) {
                MetaDataUtil.addNewIntegerMeta(town, bankLimit, Cfg.get().getInt("treasurer.limit.level-" + StewardLookup.get().getSteward(getTreasurer(town)).getLevel()), true);
            } else {
                MetaDataUtil.addNewIntegerMeta(town, bankLimit, Cfg.get().getInt("treasurer.limit.level-0"), true);
            }
        }
        return MetaDataUtil.getInt(town, bankLimitField);
    }

    public static void setBankLimit(Town town, int limit) {
        if (!MetaDataUtil.hasMeta(town, bankLimit)) {
            MetaDataUtil.addNewIntegerMeta(town, bankLimit, Cfg.get().getInt("treasurer.limit.level-0"), true);
            return;
        }
        MetaDataUtil.setInt(town, bankLimitField, limit, true);
    }


}
