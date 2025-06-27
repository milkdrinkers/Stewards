package io.github.milkdrinkers.stewards.quest.condition;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

public class HasTownConditionFactory implements PlayerConditionFactory {

    private final BetonQuestLoggerFactory loggerFactory;

    private final PrimaryServerThreadData data;

    public HasTownConditionFactory(BetonQuestLoggerFactory loggerFactory, PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(Instruction instruction) throws QuestException {
        final BetonQuestLogger logger = loggerFactory.create(HasTownCondition.class);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(new HasTownCondition(), logger, instruction.getPackage()), data);
    }
}
