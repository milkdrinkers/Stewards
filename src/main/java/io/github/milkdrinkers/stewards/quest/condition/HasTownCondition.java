package io.github.milkdrinkers.stewards.quest.condition;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.entity.Player;

public class HasTownCondition implements OnlineCondition {
    @Override
    public boolean check(OnlineProfile profile) throws QuestException {
        Player player = profile.getPlayer().getPlayer();

        if (player == null)
            throw new QuestException("Player is null.");

        Resident resident = TownyAPI.getInstance().getResident(player);

        if (resident == null)
            throw new QuestException("Player does not have a valid resident.");

        return resident.hasTown();
    }
}
