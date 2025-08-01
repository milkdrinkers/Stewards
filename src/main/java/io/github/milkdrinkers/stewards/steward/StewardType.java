package io.github.milkdrinkers.stewards.steward;

import io.github.milkdrinkers.stewards.exception.InvalidStewardTypeException;
import net.citizensnpcs.api.trait.Trait;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.milkdrinkers.stewards.towny.TownMetaData.FIELD_PREFIX;

public record StewardType(@NotNull String id, @NotNull String dataFieldKey, @NotNull String name, int maxLevel,
                          int minLevel, int startingLevel, @NotNull String settlerPrefix,
                          Class<? extends Trait> trait) {
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StewardType that)) return false;
        return Objects.equals(id(), that.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

    public static class Builder {
        private String id;
        private String name;
        private int maxLevel;
        private int minLevel;
        private int startingLevel;
        private String settlerPrefix;
        private Class<? extends Trait> trait;

        public Builder setId(@NotNull String id) {
            this.id = id;
            return this;
        }

        public Builder setName(@NotNull String name) {
            this.name = name;
            return this;
        }

        public Builder setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder setMinLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        public Builder setStartingLevel(int startingLevel) {
            this.startingLevel = startingLevel;
            return this;
        }

        public Builder setSettlerPrefix(@NotNull String settlerPrefix) {
            this.settlerPrefix = settlerPrefix;
            return this;
        }

        public Builder setTrait(@NotNull Class<? extends Trait> trait) {
            this.trait = trait;
            return this;
        }

        public StewardType build() throws InvalidStewardTypeException {
            // Check for levels less than 1
            if (maxLevel <= 0)
                maxLevel = 1;
            if (minLevel <= 0)
                minLevel = 1;
            if (minLevel > maxLevel)
                minLevel = maxLevel;
            if (startingLevel <= minLevel || startingLevel > maxLevel)
                startingLevel = 1;

            if (id == null)
                throw new InvalidStewardTypeException("StewardType Id is null");
            if (name == null)
                throw new InvalidStewardTypeException("StewardType Name is null");
            if (settlerPrefix == null)
                throw new InvalidStewardTypeException("StewardType Settler Prefix is null");
            if (trait == null)
                throw new InvalidStewardTypeException("StewardType Trait is null");

            return new StewardType(id, FIELD_PREFIX + id, name, maxLevel, minLevel, startingLevel, settlerPrefix, trait);
        }
    }
}
