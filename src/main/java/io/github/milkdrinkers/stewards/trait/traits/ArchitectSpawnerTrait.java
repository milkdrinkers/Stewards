package io.github.milkdrinkers.stewards.trait.traits;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.object.Resident;
import io.github.milkdrinkers.colorparser.paper.ColorParser;
import io.github.milkdrinkers.settlers.api.event.settler.lifetime.interact.SettlerClickedEvent;
import io.github.milkdrinkers.stewards.Stewards;
import io.github.milkdrinkers.stewards.api.StewardsAPI;
import io.github.milkdrinkers.stewards.conversation.CreateTownConversation;
import io.github.milkdrinkers.stewards.conversation.SpawnArchitectConversation;
import io.github.milkdrinkers.stewards.hook.Hook;
import io.github.milkdrinkers.stewards.utility.Logger;
import io.github.milkdrinkers.wordweaver.Translation;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ArchitectSpawnerTrait extends Trait {
    protected ArchitectSpawnerTrait() {
        super("architectspawner");
    }

    @EventHandler
    public void onClick(SettlerClickedEvent e) {
        final NPC npc = e.getSettler().getNpc();
        if (npc != this.getNPC())
            return;

        Player player = e.getClicker();
        if (StewardsAPI.getLookupArchitect().hasArchitect(player)) {
            player.sendMessage(ColorParser.of(Translation.of("traits.spawner.has-architect")).build());
            return;
        }

        final Resident resident = TownyAPI.getInstance().getResident(e.getClicker());
        if (resident == null) {
            player.sendMessage(ColorParser.of(Translation.of("error.resident-null")).build());
            Logger.get().error("Something went wrong: Resident returned null.");
            return;
        }

        if (resident.hasTown()) {
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
