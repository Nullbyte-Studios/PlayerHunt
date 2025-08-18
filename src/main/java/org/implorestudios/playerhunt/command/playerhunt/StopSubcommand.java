package org.implorestudios.playerhunt.command.playerhunt;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.game.Game;

import java.util.Collections;
import java.util.List;

public class StopSubcommand extends Command {
    public StopSubcommand(@NonNull PlayerHuntPlugin plugin) {
        super(plugin, "stop");
    }

    @Override
    protected List<String> onTabComplete(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        Game game = Game.getInstance();
        if(game.getState() != Game.GameState.IN_PROGRESS) {
            sendMessageWithPrefix(commandSender, "commands.stop.gameNotStarted");
            return true;
        }
        game.end(commandSender.getName(), true);
        return true;
    }
}
