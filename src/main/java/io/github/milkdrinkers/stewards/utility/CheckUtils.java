package io.github.milkdrinkers.stewards.utility;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.github.milkdrinkers.stewards.guard.Guard;
import io.github.milkdrinkers.stewards.hook.Hook;
import io.github.milkdrinkers.stewards.steward.Steward;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class CheckUtils {
    public static boolean canAfford(final @Nullable Town town, final @Nullable Integer cost) {
        if (town == null)
            return false;

        if (cost == null || cost <= 0)
            return true;

        return town.getAccount().canPayFromHoldings(cost);
    }

    public static void pay(final @Nullable Town town, final @Nullable Integer cost, final @Nullable String reason) {
        if (town == null)
            return;

        if (cost == null || cost <= 0)
            return;

        town.getAccount().withdraw(cost, reason != null ? reason : "");
    }

    public static boolean isAdmin(final @Nullable Player player) {
        if (player == null)
            return false;
        return Hook.getVaultHook().isHookLoaded() ? Hook.getVaultHook().getPermissions().has(player, "stewards.admin") : player.hasPermission(new Permission("stewards.admin"));
    }

    public static boolean isMayor(final @Nullable Player player) {
        if (player == null)
            return false;
        return isMayor(TownyAPI.getInstance().getResident(player));
    }

    public static boolean isMayor(final @Nullable Resident resident) {
        if (resident == null)
            return false;
        return resident.isMayor() || resident.getTownRanks().contains("co-mayor");
    }

    public static boolean isSameTown (final @Nullable Player player, final @Nullable Guard guard) {
        if (player == null)
            return false;

        if (guard == null)
            return false;

        final UUID guardTownUUID = guard.getTownUUID();
        if (guardTownUUID == null)
            return false;

        final Town playerTown = TownyAPI.getInstance().getTown(player);
        final Town guardTown = TownyAPI.getInstance().getTown(guardTownUUID);

        return isSameTown(playerTown, guardTown);
    }

    public static boolean isSameTown(final @Nullable Resident resident, final @Nullable Guard guard) {
        if (resident == null)
            return false;

        if (guard == null)
            return false;

        final UUID guardTownUUID = guard.getTownUUID();
        if (guardTownUUID == null)
            return false;

        final Town residentTown = resident.getTownOrNull();
        final Town guardTown = TownyAPI.getInstance().getTown(guardTownUUID);

        return isSameTown(residentTown, guardTown);
    }

    public static boolean isSameTown(final @Nullable Player player, final @Nullable Steward steward) {
        if (player == null)
            return false;

        if (steward == null)
            return false;

        final UUID stewardTownUUID = steward.getTownUUID();
        if (stewardTownUUID == null)
            return false;

        final Town playerTown = TownyAPI.getInstance().getTown(player);
        final Town stewardTown = TownyAPI.getInstance().getTown(stewardTownUUID);

        return isSameTown(playerTown, stewardTown);
    }

    public static boolean isSameTown(final @Nullable Resident resident, final @Nullable Steward steward) {
        if (resident == null)
            return false;

        if (steward == null)
            return false;

        final UUID stewardTownUUID = steward.getTownUUID();
        if (stewardTownUUID == null)
            return false;

        final Town residentTown = resident.getTownOrNull();
        final Town stewardTown = TownyAPI.getInstance().getTown(stewardTownUUID);

        return isSameTown(residentTown, stewardTown);
    }

    public static boolean isSameTown(final @Nullable Town town1, final @Nullable Town town2) {
        if (town1 == null)
            return false;

        if (town2 == null)
            return false;

        return town1.equals(town2);
    }
}
