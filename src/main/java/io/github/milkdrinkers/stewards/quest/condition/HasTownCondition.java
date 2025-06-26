package io.github.milkdrinkers.stewards.quest.condition;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

public class HasTownCondition implements OnlineCondition {
    @Override
    public boolean check(OnlineProfile profile) throws QuestRuntimeException {
        Player player = profile.getPlayer().getPlayer();

        if (player == null)
            throw new QuestRuntimeException("Player is null.");

        Resident resident = TownyAPI.getInstance().getResident(player);

        if (resident == null)
            throw new QuestRuntimeException("Player does not have a valid resident.");

        return resident.hasTown();
    }
}
