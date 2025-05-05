package io.github.milkdrinkers.stewards.utility;

import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.steward.Steward;
import net.citizensnpcs.trait.SkinTrait;

import java.util.ArrayList;
import java.util.List;

public class Appearance {

    private static final List<String> maleStewardSkinKeys = new ArrayList<>();
    private static final List<String> femaleStewardSkinKeys = new ArrayList<>();

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
