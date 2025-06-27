package io.github.milkdrinkers.stewards.quest.condition;

import io.github.milkdrinkers.stewards.steward.StewardLookup;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.entity.Player;

public class HasArchitectCondition implements OnlineCondition {
    @Override
    public boolean check(OnlineProfile profile) throws QuestException {
        Player player = profile.getPlayer().getPlayer();

        if (player == null)
            throw new QuestException("Player is null.");

        return StewardLookup.get().hasArchitect(player);
    }
}
