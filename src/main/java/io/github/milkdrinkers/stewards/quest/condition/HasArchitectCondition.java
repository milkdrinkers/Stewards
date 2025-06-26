package io.github.milkdrinkers.stewards.quest.condition;

import io.github.milkdrinkers.stewards.steward.StewardLookup;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

public class HasArchitectCondition implements OnlineCondition {
    @Override
    public boolean check(OnlineProfile profile) throws QuestRuntimeException {
        Player player = profile.getPlayer().getPlayer();

        if (player == null)
            throw new QuestRuntimeException("Player is null.");

        return StewardLookup.get().hasArchitect(player);
    }
}
