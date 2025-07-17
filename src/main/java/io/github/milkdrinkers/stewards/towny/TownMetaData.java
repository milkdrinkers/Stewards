package io.github.milkdrinkers.stewards.towny;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.LongDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.steward.Steward;
import io.github.milkdrinkers.stewards.steward.StewardType;
import io.github.milkdrinkers.stewards.steward.StewardTypeHandler;
import io.github.milkdrinkers.stewards.utility.Cfg;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.milkdrinkers.stewards.steward.StewardTypeHandler.*;

public class TownMetaData {
    public static final String FIELD_PREFIX = "stewards_";

    private static final String BANK_LIMIT = FIELD_PREFIX + "treasurer_bank_limit";
    private static final String HIRING_STEWARD = FIELD_PREFIX + "unhired_steward";

    private static final String ARCHITECT = FIELD_PREFIX + ARCHITECT_ID;
    private static final String BAILIFF = FIELD_PREFIX + BAILIFF_ID;
    private static final String PORTMASTER = FIELD_PREFIX + PORTMASTER_ID;
    private static final String STABLEMASTER = FIELD_PREFIX + STABLEMASTER_ID;
    private static final String TREASURER = FIELD_PREFIX + TREASURER_ID;

    private static final LongDataField BANK_LIMIT_FIELD = new LongDataField(BANK_LIMIT);
    private static final BooleanDataField HIRING_STEWARD_FIELD = new BooleanDataField(HIRING_STEWARD);

    // Steward tracking
    private static final StringDataField ARCHITECT_FIELD = new StringDataField(ARCHITECT);
    private static final StringDataField BAILIFF_FIELD = new StringDataField(BAILIFF);
    private static final StringDataField PORTMASTER_FIELD = new StringDataField(PORTMASTER);
    private static final StringDataField STABLEMASTER_FIELD = new StringDataField(STABLEMASTER);
    private static final StringDataField TREASURER_FIELD = new StringDataField(TREASURER);

    private static final Map<String, StringDataField> STEWARD_FIELDS = Map.of(
        ARCHITECT, ARCHITECT_FIELD,
        BAILIFF, BAILIFF_FIELD,
        PORTMASTER, PORTMASTER_FIELD,
        STABLEMASTER, STABLEMASTER_FIELD,
        TREASURER, TREASURER_FIELD
    );

    public static class NPC {
        // Has methods

        /**
         * Check whether a town has any type of steward
         *
         * @param town town
         * @return true if any single type of steward exists, otherwise false
         */
        public static boolean hasSteward(Town town) {
            for (StewardType type : StewardsAPI.getRegistry().getValues()) {
                if (has(town, type))
                    return true;
            }
            return false;
        }

        private static StringDataField getFieldFromKey(String key) {
            return STEWARD_FIELDS.get(key);
        }

        public static boolean has(Town town, StewardType type) {
            final StringDataField field = getFieldFromKey(type.dataFieldKey());
            return MetaUtil.hasUUID(town, field);
        }

        // Get methods

        /**
         * Get a set containing the UUIDs of all stewards in this town.
         *
         * @param town the town
         * @return set of steward UUIDs
         */
        public static Set<UUID> getStewards(Town town) {
            return StewardsAPI.getRegistry().getValues().stream()
                .filter(type -> has(town, type))
                .map(type -> get(town, type))
                .collect(Collectors.toSet());
        }

        public static @Nullable UUID get(Town town, StewardType type) {
            final StringDataField field = getFieldFromKey(type.dataFieldKey());
            return MetaUtil.getUUID(town, field);
        }

        public static @Nullable Steward getSteward(Town town, StewardType type) {
            final StringDataField field = getFieldFromKey(type.dataFieldKey());
            final UUID uuid = MetaUtil.getUUID(town, field);
            if (uuid == null)
                return null;

            return StewardsAPI.getLookup().get(uuid);
        }

        public static Optional<Steward> getStewardOptional(Town town, StewardType type) {
            return Optional.ofNullable(getSteward(town, type));
        }

        // Set methods

        public static void set(Town town, UUID uuid, StewardType type) {
            final StringDataField field = getFieldFromKey(type.dataFieldKey());
            MetaUtil.putUUID(town, field, uuid, true);
        }

        public static void set(Town town, Steward steward) {
            set(town, steward.getUniqueId(), steward.getStewardType());
        }

        // Remove methods

        public static void remove(Town town, StewardType type) {
            final StringDataField field = getFieldFromKey(type.dataFieldKey());
            MetaUtil.removeUUID(town, field, true);
        }

        public static void remove(Town town, Steward steward) {
            remove(town, steward.getStewardType());
        }
    }

    /**
     * Whether this town is actively trying to hire a new steward (Have a unhired one spawned)
     *
     * @param town the town
     * @return is hiring
     */
    public static boolean isHiringSteward(Town town) {
        if (!MetaDataUtil.hasMeta(town, HIRING_STEWARD))
            MetaDataUtil.addNewBooleanMeta(town, HIRING_STEWARD, false, true);
        return MetaDataUtil.getBoolean(town, HIRING_STEWARD_FIELD);
    }

    /**
     * Set whether this town is actively trying to hire a new steward (Have a unhired one spawned)
     *
     * @param town  the town
     * @param value the state
     */
    public static void setHiringSteward(Town town, boolean value) {
        if (!MetaDataUtil.hasMeta(town, HIRING_STEWARD))
            MetaDataUtil.addNewBooleanMeta(town, HIRING_STEWARD, false, true);
        MetaDataUtil.setBoolean(town, HIRING_STEWARD_FIELD, value, true);
    }

    public static long getBankLimit(Town town) {
        final StewardType type = StewardsAPI.getRegistry().getType(StewardTypeHandler.TREASURER_ID);

        if (!MetaDataUtil.hasMeta(town, BANK_LIMIT_FIELD)) {
            if (NPC.has(town, type)) {
                setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-" + NPC.getStewardOptional(town, type).map(Steward::getLevel).orElse(1)));
            } else {
                setBankLimit(town, Cfg.get().getInt("treasurer.limit.level-0"));
            }
        }
        return MetaDataUtil.getLong(town, BANK_LIMIT_FIELD);
    }

    public static void setBankLimit(Town town, long limit) {
        if (!MetaDataUtil.hasMeta(town, BANK_LIMIT_FIELD)) {
            MetaDataUtil.addNewLongMeta(town, BANK_LIMIT_FIELD.getKey(), limit, true);
        } else {
            MetaDataUtil.setLong(town, BANK_LIMIT_FIELD, limit, true);
        }
    }
}
