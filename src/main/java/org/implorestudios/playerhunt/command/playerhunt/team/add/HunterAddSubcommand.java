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

public class HunterAddSubcommand extends Command {
    public HunterAddSubcommand(@NonNull PlayerHuntPlugin plugin) {
        super(plugin, "hunter");
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
        Game game = Game.getInstance();
        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class).getLanguageManager().sendMessageWithPrefix(commandSender, "commands.team.add.notFound");
            return true;
        }
        if(game.getPlayerManager().isPlayer(player)) {
            PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class).getLanguageManager().sendMessageWithPrefix(commandSender, "commands.team.add.alreadyInTeam");
            return true;
        }
        game.getPlayerManager().addHunter(player);
        PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class).getLanguageManager().sendMessageWithPrefix(commandSender, "commands.team.add.hunter", new StringReplacer("%player%", player.getName()));
        return true;
    }
}
