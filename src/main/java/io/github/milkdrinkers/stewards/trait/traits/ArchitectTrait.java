package io.github.milkdrinkers.stewards.trait.traits;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

import java.time.Instant;
import java.util.UUID;

public class ArchitectTrait extends Trait {
    protected ArchitectTrait() {
        super("architect");
    }

    UUID spawningPlayer;
    @Persist String spawningPlayerString;
    Instant createTime;
    @Persist long createTimeLong;

    public void load(DataKey key) {
        spawningPlayer = UUID.fromString(key.getString("spawningplayer"));
        spawningPlayerString = key.getString("spawningplayer");
        createTime = Instant.ofEpochSecond(key.getLong("createtime"));
        createTimeLong = key.getLong("createtime");
    }

    public void save(DataKey key) {
        key.setString("spawningplayer", spawningPlayerString);
        key.setLong("createtime", createTimeLong);
    }

    public UUID getSpawningPlayer() {
        return spawningPlayer;
    }

    public void setSpawningPlayer(UUID spawningPlayer) {
        this.spawningPlayer = spawningPlayer;
        this.spawningPlayerString = spawningPlayer.toString();
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
        this.createTimeLong = createTime.getEpochSecond();
    }

}
