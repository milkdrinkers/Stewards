package io.github.milkdrinkers.stewards.towny;

import com.palmergames.bukkit.towny.object.TownyObject;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

final class MetaUtil {
    public static void putUUID(TownyObject object, StringDataField df, UUID uuid, boolean save) {
        if (!MetaDataUtil.hasMeta(object, df))
            addUUID(object, df.getKey(), false);
        setUUID(object, df, uuid, save);
    }

    public static void addUUID(TownyObject object, String key, boolean save) {
        MetaDataUtil.addNewStringMeta(object, key, "", save);
    }

    public static void setUUID(TownyObject object, StringDataField df, UUID uuid, boolean save) {
        MetaDataUtil.setString(object, df, uuid.toString(), save);
    }

    public static void removeUUID(TownyObject object, StringDataField df, boolean save) {
        object.removeMetaData(df, save);
    }

    public static boolean hasUUID(TownyObject object, StringDataField df) {
        return object.hasMeta(df.getKey(), df.getClass()) &&
            MetaDataUtil.getString(object, df) != null && // TODO Backwards compatible patches for when meta got set to empty or null
            !MetaDataUtil.getString(object, df).isEmpty(); // TODO Do a cleanup on fields on startup instead
    }

    public static @Nullable UUID getUUID(TownyObject object, StringDataField df) {
        if (!hasUUID(object, df))
            return null;

        final String storedString = MetaDataUtil.getString(object, df);
        return UUID.fromString(storedString);
    }
}
