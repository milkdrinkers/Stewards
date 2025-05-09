package io.github.milkdrinkers.stewards.steward;

import com.palmergames.bukkit.towny.object.TownBlock;
import io.github.milkdrinkers.settlers.api.settler.AbstractSettler;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.exception.InvalidStewardException;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Steward {
    private final StewardType stewardType;
    private final AbstractSettler settler;
    private TownBlock townBlock;
    private int level;
    private UUID townUUID;
    private final boolean isEnabled;
    private final boolean isHidden;
    private final double dailyUpkeepCost;

    public Steward(StewardType stewardType, AbstractSettler settler, TownBlock townBlock, int level, UUID townUUID, boolean isEnabled, boolean isHidden, double dailyUpkeepCost) {
        this.stewardType = stewardType;
        this.settler = settler;
        this.townBlock = townBlock;
        this.level = level;
        this.townUUID = townUUID;
        this.isEnabled = isEnabled;
        this.isHidden = isHidden;
        this.dailyUpkeepCost = dailyUpkeepCost;
    }

    public StewardType getStewardType() {
        return stewardType;
    }

    public AbstractSettler getSettler() {
        return settler;
    }

    public TownBlock getTownBlock() {
        return townBlock;
    }

    public void setTownBlock(TownBlock townBlock) {
        this.townBlock = townBlock;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void levelUp() {
        level++;
    }

    public UUID getTownUUID() {
        return townUUID;
    }

    public void setTownUUID(UUID townUUID) {
        this.townUUID = townUUID;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public double getDailyUpkeepCost() {
        return dailyUpkeepCost;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private StewardType stewardType;
        private AbstractSettler settler;
        private TownBlock townBlock;
        private int level;
        private UUID townUUID;
        private boolean isEnabled;
        private boolean isHidden;
        private double dailyUpkeepCost;

        public Builder setStewardType(@NotNull StewardType stewardType) {
            this.stewardType = stewardType;
            return this;
        }

        public Builder setSettler(@NotNull AbstractSettler settler) {
            this.settler = settler;
            return this;
        }

        public Builder setTownBlock(@NotNull TownBlock townBlock) {
            this.townBlock = townBlock;
            return this;
        }

        public Builder setLevel(int level) {
            this.level = level;
            return this;
        }

        public Builder setTownUUID(@NotNull UUID townUUID) {
            this.townUUID = townUUID;
            return this;
        }

        public Builder setIsEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public Builder setIsHidden(boolean isHidden) {
            this.isHidden = isHidden;
            return this;
        }

        public Builder setDailyUpkeepCost(double dailyUpkeepCost) {
            this.dailyUpkeepCost = dailyUpkeepCost;
            return this;
        }

        public Steward build() throws InvalidStewardException {

            if (level <= 0)
                level = 1;

            if (stewardType == null)
                throw new InvalidStewardException("StewardType is null");
            // TODO: API
            if (!Stewards.getInstance().getStewardTypeHandler().getStewardTypeRegistry().isRegistered(stewardType))
                throw new InvalidStewardException("StewardType is not registered");
            
            if (settler == null)
                throw new InvalidStewardException("Settler is null");

            return new Steward(stewardType, settler, townBlock, level, townUUID, isEnabled, isHidden, dailyUpkeepCost);
        }
    }
}
