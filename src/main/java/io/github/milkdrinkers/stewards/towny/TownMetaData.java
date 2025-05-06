package io.github.milkdrinkers.stewards.towny;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;
import io.github.milkdrinkers.stewards.utility.Cfg;

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

    private static final IntegerDataField bankLimitField = new IntegerDataField(bankLimit);

    private static final BooleanDataField unhiredStewardField = new BooleanDataField(unhiredSteward);

    private static final StringDataField architectField = new StringDataField(architect);
    private static final StringDataField bailiffField = new StringDataField(bailiff);
    private static final StringDataField portmasterField = new StringDataField(portmaster);
    private static final StringDataField stablemasterField = new StringDataField(stablemaster);
    private static final StringDataField treasurerField = new StringDataField(treasurer);

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

    public static void setArchitect(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, architect))
            MetaDataUtil.addNewStringMeta(town, architect, "", true);
        MetaDataUtil.setString(town, architectField, uuid.toString(), true);
    }

    public static void setBailiff(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, bailiff))
            MetaDataUtil.addNewStringMeta(town, bailiff, "", true);
        MetaDataUtil.setString(town, bailiffField, uuid.toString(), true);
    }

    public static void setPortmaster(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, portmaster))
            MetaDataUtil.addNewStringMeta(town, portmaster, "", true);
        MetaDataUtil.setString(town, portmasterField, uuid.toString(), true);
    }

    public static void setStablemaster(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, stablemaster))
            MetaDataUtil.addNewStringMeta(town, stablemaster, "", true);
        MetaDataUtil.setString(town, stablemasterField, uuid.toString(), true);
    }

    public static void setTreasurer(Town town, UUID uuid) {
        if (!MetaDataUtil.hasMeta(town, treasurer))
            MetaDataUtil.addNewStringMeta(town, treasurer, "", true);
        MetaDataUtil.setString(town, treasurerField, uuid.toString(), true);
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
        if (!MetaDataUtil.hasMeta(town, bankLimit))
            MetaDataUtil.addNewIntegerMeta(town, bankLimit, Cfg.get().getInt("treasurer.limit.level-0"), true); // TODO Get level of banker and set limit
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
