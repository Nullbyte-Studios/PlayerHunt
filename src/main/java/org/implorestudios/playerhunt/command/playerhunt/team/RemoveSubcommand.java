package org.implorestudios.playerhunt.command.playerhunt.team;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.game.Game;
import org.implorestudios.playerhunt.language.StringReplacer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RemoveSubcommand extends Command {
    public RemoveSubcommand(@NonNull JavaPlugin plugin) {
        super(plugin, "remove");
    }

    @Override
    protected List<String> onTabComplete(@NonNull CommandSender sender, String @NonNull [] args) {
        args[0] = args.length == 0 ? "" : args[0];
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.contains(args[0])).collect(Collectors.toList());
    }
    
    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] args) {
        if(args.length == 0) {
            Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "helpHeader", new StringReplacer("%command%", "/playerhunt remove"));
            Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "commands.team.remove.help");
            return true;
        }
        Game game = Game.getInstance();
        if(game.getState() == Game.GameState.COUNTDOWN) {
            Objects.requireNonNull(getLanguageManager()).sendMessageWithPrefix(commandSender, "commands.remove.inCountdown");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        game.getPlayerManager().removePlayer(player);
        Objects.requireNonNull(getLanguageManager()).sendMessageWithPrefix(commandSender, "commands.team.remove.success", new StringReplacer("%player%", player.getName()));
        return true;
    }
}
