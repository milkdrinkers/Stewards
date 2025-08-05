package io.github.milkdrinkers.stewards.utility;

import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.guard.Guard;
import io.github.milkdrinkers.stewards.steward.Steward;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;

import java.util.ArrayList;
import java.util.List;

public class Appearance {

    private static final List<String> maleStewardSkinKeys = new ArrayList<>();
    private static final List<String> femaleStewardSkinKeys = new ArrayList<>();
    private static final List<String> maleGuardSkinKeys = new ArrayList<>();
    private static final List<String> femaleGuardSkinKeys = new ArrayList<>();

    public static void applyMaleStewardSkin(Steward steward) {
        if (maleStewardSkinKeys.isEmpty())
            Stewards.getInstance().getConfigHandler().getSkinsCfg().getMap("male-steward").keySet()
                .forEach(k -> maleStewardSkinKeys.add(k.toString()));

        String key = maleStewardSkinKeys.get(randomInt(maleStewardSkinKeys.size()));

        steward.getSettler().getNpc().getOrAddTrait(SkinTrait.class).setSkinPersistent(key, getStewardMaleSkinSignature(key), getStewardMaleSkinValue(key));
    }

    public static void applyFemaleStewardSkin(Steward steward) {
        if (femaleStewardSkinKeys.isEmpty())
            Stewards.getInstance().getConfigHandler().getSkinsCfg().getMap("female-steward").keySet()
                .forEach(k -> femaleStewardSkinKeys.add(k.toString()));

        String key = femaleStewardSkinKeys.get(randomInt(femaleStewardSkinKeys.size()));

        steward.getSettler().getNpc().getOrAddTrait(SkinTrait.class).setSkinPersistent(key, getStewardFemaleSkinSignature(key), getStewardFemaleSkinValue(key));
    }

    public static void applyMaleGuardSkin(NPC npc) {
        if (maleGuardSkinKeys.isEmpty())
            Stewards.getInstance().getConfigHandler().getSkinsCfg().getMap("male-guard").keySet()
                .forEach(k -> maleGuardSkinKeys.add(k.toString()));

        String key = maleGuardSkinKeys.get(randomInt(maleGuardSkinKeys.size()));

        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(key, getGuardMaleSkinSignature(key), getGuardMaleSkinValue(key));
    }

    public static void applyMaleGuardSkin(Steward steward) {
        applyMaleGuardSkin(steward.getNpc());
    }

    public static void applyMaleGuardSkin(Guard guard) {
        applyMaleGuardSkin(guard.getNpc());
    }

    public static void applyFemaleGuardSkin(NPC npc) {
        if (femaleGuardSkinKeys.isEmpty())
            Stewards.getInstance().getConfigHandler().getSkinsCfg().getMap("female-guard").keySet()
                .forEach(k -> femaleGuardSkinKeys.add(k.toString()));

        String key = femaleGuardSkinKeys.get(randomInt(femaleGuardSkinKeys.size()));

        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(key, getGuardFemaleSkinSignature(key), getGuardFemaleSkinValue(key));
    }

    public static void applyFemaleGuardSkin(Steward steward) {
        applyFemaleGuardSkin(steward.getNpc());
    }

    public static void applyFemaleGuardSkin(Guard guard) {
        applyFemaleGuardSkin(guard.getNpc());
    }

    private static String getStewardMaleSkinValue(String skinName) {
        return Stewards.getInstance().getConfigHandler().getSkinsCfg().getString("male-steward." + skinName + ".skin-value");
    }

    private static String getStewardMaleSkinSignature(String skinName) {
        return Stewards.getInstance().getConfigHandler().getSkinsCfg().getString("male-steward." + skinName + ".skin-signature");
    }

    private static String getStewardFemaleSkinValue(String skinName) {
        return Stewards.getInstance().getConfigHandler().getSkinsCfg().getString("female-steward." + skinName + ".skin-value");
    }

    private static String getStewardFemaleSkinSignature(String skinName) {
        return Stewards.getInstance().getConfigHandler().getSkinsCfg().getString("female-steward." + skinName + ".skin-signature");
    }

    private static String getGuardMaleSkinValue(String skinName) {
        return Stewards.getInstance().getConfigHandler().getSkinsCfg().getString("male-guard." + skinName + ".skin-value");
    }

    private static String getGuardMaleSkinSignature(String skinName) {
        return Stewards.getInstance().getConfigHandler().getSkinsCfg().getString("male-guard." + skinName + ".skin-signature");
    }

    private static String getGuardFemaleSkinValue(String skinName) {
        return Stewards.getInstance().getConfigHandler().getSkinsCfg().getString("female-guard." + skinName + ".skin-value");
    }

    private static String getGuardFemaleSkinSignature(String skinName) {
        return Stewards.getInstance().getConfigHandler().getSkinsCfg().getString("female-guard." + skinName + ".skin-signature");
    }

    private static String getRandomName() {
        if (Math.random() > 0.5)
            return getFemaleName();
        return getMaleName();
    }

    public static String getMaleName() {
        List<String> firstNameList = Stewards.getInstance().getConfigHandler().getNameCfg().getStringList("male-names");

        String firstName = firstNameList.get(randomInt(firstNameList.size()));

        List<String> lastNameList = Stewards.getInstance().getConfigHandler().getNameCfg().getStringList("last-names");

        String lastName = lastNameList.get(randomInt(lastNameList.size()));

        return firstName + " " + lastName;
    }

    public static String getFemaleName() {
        List<String> firstNameList = Stewards.getInstance().getConfigHandler().getNameCfg().getStringList("female-names");

        String firstName = firstNameList.get(randomInt(firstNameList.size()));

        List<String> lastNameList = Stewards.getInstance().getConfigHandler().getNameCfg().getStringList("last-names");

        String lastName = lastNameList.get(randomInt(lastNameList.size()));

        return firstName + " " + lastName;
    }


    public static int randomInt(int max) {
        return (int) ((Math.random() * (max) + 0));
    }
}
