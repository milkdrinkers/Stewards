package io.github.milkdrinkers.stewards.quest.condition;

import com.palmergames.bukkit.towny.TownySettings;
import io.github.milkdrinkers.stewards.hook.Hook;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.entity.Player;

public class CanAffordArchitectCondition implements OnlineCondition {
    @Override
    public boolean check(OnlineProfile profile) throws QuestException {
        Player player = profile.getPlayer().getPlayer();

        if (player == null)
            throw new QuestException("Player is null.");

        return Hook.getVaultHook().getEconomy().getBalance(player) > TownySettings.getNewTownPrice();
    }
}
