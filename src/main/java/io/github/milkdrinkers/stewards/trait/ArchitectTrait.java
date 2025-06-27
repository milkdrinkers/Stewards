package io.github.milkdrinkers.stewards.trait;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

import java.time.Instant;
import java.util.UUID;

public class ArchitectTrait extends Trait {
    protected ArchitectTrait() {
        super("architect");
    }

    @Persist UUID spawningPlayer;
    @Persist Instant createTime;

    public void load(DataKey key) {
        spawningPlayer = UUID.fromString(key.getString("spawningplayer"));
        createTime = Instant.ofEpochSecond(key.getLong("createtime"));
    }

    public void save(DataKey key) {
        key.setString("spawningplayer", spawningPlayer.toString());
        key.setLong("createtime", createTime.getEpochSecond());
    }

    public UUID getSpawningPlayer() {
        return spawningPlayer;
    }

    public void setSpawningPlayer(UUID spawningPlayer) {
        this.spawningPlayer = spawningPlayer;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

}
