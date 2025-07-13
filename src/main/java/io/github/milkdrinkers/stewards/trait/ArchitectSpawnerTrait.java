package io.github.milkdrinkers.stewards.trait;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.conversation.CreateTownConversation;
import io.github.milkdrinkers.stewards.conversation.SpawnArchitectConversation;
import io.github.milkdrinkers.stewards.hook.Hook;
import io.github.milkdrinkers.stewards.steward.StewardLookup;
import io.github.milkdrinkers.stewards.utility.Logger;
import io.github.milkdrinkers.wordweaver.Translation;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ArchitectSpawnerTrait extends Trait {

    protected ArchitectSpawnerTrait() {
        super ("architectspawner");
    }


    @EventHandler
    public void onClick(NPCRightClickEvent e) {
        if (e.getNPC() != this.getNPC()) return;

        Player player = e.getClicker();
        if (StewardLookup.get().hasArchitect(player)) {
            player.sendMessage(ColorParser.of(Translation.of("traits.spawner.has-architect")).build());
            return;
        }

        if (TownyAPI.getInstance().getResident(player) == null) {
            player.sendMessage(ColorParser.of(Translation.of("error.resident-null")).build());
            Logger.get().error("Something went wrong: Resident returned null.");
            return;
        }

        if (TownyAPI.getInstance().getResident(player).hasTown()) {
            player.sendMessage(ColorParser.of(Translation.of("traits.spawner.has-town")).build());
            return;
        }

        if (Hook.getVaultHook().getEconomy().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())) < TownySettings.getNewTownPrice()) {
            player.sendMessage(ColorParser.of(Translation.of("traits.spawner.cannot-afford").formatted(String.valueOf(TownySettings.getNewTownPrice()))).build());
            return;
        }

        ConversationFactory factory = new ConversationFactory(Stewards.getInstance()).withPrefix(CreateTownConversation.getPrefix).withLocalEcho(false);
        factory.withFirstPrompt(SpawnArchitectConversation.getSpawnArchitectPrompt(player, this.getNPC().getStoredLocation().add(1, 0, 0))).buildConversation(player).begin();
    }
}
