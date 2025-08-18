package org.implorestudios.playerhunt.command.playerhunt.team;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.game.Game;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LastSubcommand extends Command {
    public LastSubcommand(@NonNull JavaPlugin plugin) {
        super(plugin, "last");
    }

    @Override
    protected List<String> onTabComplete(@NonNull CommandSender sender, String @NonNull [] args) {
        args[0] = args.length == 0 ? "" : args[0];
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] args) {
        Game game = Game.getInstance();
        game.getPlayerManager().setLastPlayers();
        Objects.requireNonNull(getLanguageManager()).sendMessageWithPrefix(commandSender, "commands.team.last");
        return true;
    }
}