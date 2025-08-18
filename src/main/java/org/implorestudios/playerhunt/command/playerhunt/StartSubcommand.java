package org.implorestudios.playerhunt.command.playerhunt;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.game.Game;
import org.implorestudios.playerhunt.language.IntegerReplacer;

import java.util.Collections;
import java.util.List;

public class StartSubcommand extends Command {
    private final PlayerHuntPlugin plugin;
    public StartSubcommand(@NonNull PlayerHuntPlugin plugin) {
        super(plugin, "start");
        this.plugin = plugin;
    }

    @Override
    protected List<String> onTabComplete(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        Game game = Game.getInstance();
        if(game.getState() != Game.GameState.NOT_STARTED) {
            sendMessageWithPrefix(commandSender, "commands.start.gameAlreadyStarted");
            return true;
        }
        if(game.getPlayerManager().getHunters().isEmpty() || game.getPlayerManager().getRunners().isEmpty()) {
            sendMessageWithPrefix(commandSender, "commands.start.notEnoughPlayers");
            return true;
        }
        game.init();
        sendMessageWithPrefix(commandSender, "commands.start.starting", new IntegerReplacer("%time%", plugin.getMainConfig().getInt("countdown")));
        return true;
    }
}
