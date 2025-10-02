package io.github.milkdrinkers.stewards.quest;

import io.github.milkdrinkers.stewards.Reloadable;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.quest.condition.CanAffordArchitectConditionFactory;
import io.github.milkdrinkers.stewards.quest.condition.HasArchitectConditionFactory;
import io.github.milkdrinkers.stewards.quest.condition.HasTownConditionFactory;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;

public class BetonQuestHandler implements Reloadable {
    @Override
    public void onLoad(Stewards plugin) {

    }

    @Override
    public void onEnable(Stewards plugin) {
        final BetonQuestLoggerFactory loggerFactory = BetonQuest.getInstance().getLoggerFactory();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(plugin.getServer(), plugin.getServer().getScheduler(), plugin);

        BetonQuest.getInstance().getQuestRegistries().condition().register("stewardshastown", new HasTownConditionFactory(loggerFactory, data));
        BetonQuest.getInstance().getQuestRegistries().condition().register("stewardshasarchitect", new HasArchitectConditionFactory(loggerFactory, data));
        BetonQuest.getInstance().getQuestRegistries().condition().register("stewardscanaffordarchitect", new CanAffordArchitectConditionFactory(loggerFactory, data));
    }

    @Override
    public void onDisable(Stewards plugin) {

    }
}
