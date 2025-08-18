package org.implorestudios.playerhunt.command.playerhunt.team.add;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.game.Game;
import org.implorestudios.playerhunt.language.StringReplacer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RunnerAddSubcommand extends Command {
    public RunnerAddSubcommand(@NonNull PlayerHuntPlugin plugin) {
        super(plugin, "runner");
    }
    @Override
    protected List<String> onTabComplete(@NonNull CommandSender sender, String @NonNull [] args) {
        args[0] = args.length == 0 ? "" : args[0];
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.contains(args[0])).collect(Collectors.toList());
    }
    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] args) {
        if(args.length == 0) {
            Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "helpHeader", new StringReplacer("%command%", "/playerhunt add"));
            Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "commands.team.add.help");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        Game game = Game.getInstance();
        if(player == null) {
            PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class).getLanguageManager().sendMessageWithPrefix(commandSender, "commands.team.add.notFound");
            return true;
        }
        if(game.getPlayerManager().isPlayer(player)) {
            PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class).getLanguageManager().sendMessageWithPrefix(commandSender, "commands.team.add.alreadyInTeam");
            return true;
        }
        game.getPlayerManager().addRunner(player);
        PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class).getLanguageManager().sendMessageWithPrefix(commandSender, "commands.team.add.runner", new StringReplacer("%player%", player.getName()));
        return true;
    }
}
