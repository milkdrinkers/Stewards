package io.github.milkdrinkers.stewards.trait.traits.guard;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuardCaptainTrait extends Trait {

    @Persist
    private List<UUID> guards = new ArrayList<>();

    @Persist
    private int guardCount;

    protected GuardCaptainTrait() {
        super("guardcaptain");
    }

    public List<UUID> getGuards() {
        return guards;
    }

    public void setGuards(List<UUID> guards) {
        this.guards = guards;
    }

    public void clearGuards() {
        guards.clear();
    }

    public int getGuardCount() {
        return guardCount;
    }

    public void setGuardCount(int guardCount) {
        this.guardCount = guardCount;
    }

    public void addGuard(UUID guard) {
        guards.add(guard);
        guardCount++;
    }

    public void removeGuard(UUID guard) {
        guards.remove(guard);
        guardCount--;
    }
}
