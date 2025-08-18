package org.implorestudios.playerhunt.command.playerhunt.team;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.game.Game;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClearSubcommand extends Command {
    public ClearSubcommand(@NonNull JavaPlugin plugin) {
        super(plugin, "clear");
    }

    @Override
    protected List<String> onTabComplete(@NonNull CommandSender sender, String @NonNull [] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] args) {
        Game game = Game.getInstance();
        for (Player player : game.getPlayerManager().getPlayers()) {
            game.getPlayerManager().removePlayer(player);
        }
        Objects.requireNonNull(getLanguageManager()).sendMessageWithPrefix(commandSender, "commands.team.clear");
        return true;
    }
}
