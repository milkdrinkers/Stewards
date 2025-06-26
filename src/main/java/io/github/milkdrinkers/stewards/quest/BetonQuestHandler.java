package io.github.milkdrinkers.stewards.quest;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.quest.condition.HasTownConditionFactory;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;

public class BetonQuestHandler implements Reloadable {
    @Override
    public void onLoad(Stewards plugin) {

    }

    @Override
    public void onEnable(Stewards plugin) {
        final BetonQuestLoggerFactory loggerFactory = BetonQuest.getInstance().getLoggerFactory();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(plugin.getServer(), plugin.getServer().getScheduler(), plugin);

        BetonQuest.getInstance().getQuestRegistries().getConditionTypes().register("stewardhastown", new HasTownConditionFactory(loggerFactory, data));
    }

    @Override
    public void onDisable(Stewards plugin) {

    }
}
