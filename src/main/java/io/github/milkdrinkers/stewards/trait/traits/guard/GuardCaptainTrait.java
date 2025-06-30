package io.github.milkdrinkers.stewards.trait.traits.guard;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

import java.util.UUID;

public class GuardCaptainTrait extends Trait {

    private UUID guardOne;
    private UUID guardTwo;
    private UUID guardThree;
    private UUID guardFour;
    private UUID guardFive;
    private UUID guardSix;

    protected GuardCaptainTrait() {
        super("guardcaptain");
    }

    public void load(DataKey key) {
        guardOne = UUID.fromString(key.getString("guardOne"));
        guardTwo = UUID.fromString(key.getString("guardTwo"));
        guardThree = UUID.fromString(key.getString("guardThree"));
        guardFour = UUID.fromString(key.getString("guardFour"));
        guardFive = UUID.fromString(key.getString("guardFive"));
        guardSix = UUID.fromString(key.getString("guardSix"));
    }

    public void save(DataKey key) {
        key.setString("guardOne", guardOne.toString());
        key.setString("guardTwo", guardTwo.toString());
        key.setString("guardThree", guardThree.toString());
        key.setString("guardFour", guardFour.toString());
        key.setString("guardFive", guardFive.toString());
        key.setString("guardSix", guardSix.toString());
    }

    public UUID getGuardOne() {
        return guardOne;
    }

    public void setGuardOne(UUID guardOne) {
        this.guardOne = guardOne;
    }

    public UUID getGuardTwo() {
        return guardTwo;
    }

    public void setGuardTwo(UUID guardTwo) {
        this.guardTwo = guardTwo;
    }

    public UUID getGuardThree() {
        return guardThree;
    }

    public void setGuardThree(UUID guardThree) {
        this.guardThree = guardThree;
    }

    public UUID getGuardFour() {
        return guardFour;
    }

    public void setGuardFour(UUID guardFour) {
        this.guardFour = guardFour;
    }

    public UUID getGuardFive() {
        return guardFive;
    }

    public void setGuardFive(UUID guardFive) {
        this.guardFive = guardFive;
    }

    public UUID getGuardSix() {
        return guardSix;
    }

    public void setGuardSix(UUID guardSix) {
        this.guardSix = guardSix;
    }
}
