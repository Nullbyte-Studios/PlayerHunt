package org.implorestudios.playerhunt.command.playerhunt;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.command.playerhunt.team.TeamSubcommand;
import org.implorestudios.playerhunt.language.StringReplacer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PlayerhuntCommand extends Command {
    public PlayerhuntCommand(@NonNull PlayerHuntPlugin plugin) {
        super(plugin, "playerhunt");
        setPermissionName("playerhunt.command");
        addSubCommand(new StartSubcommand(plugin));
        addSubCommand(new StopSubcommand(plugin));
        addSubCommand(new TeamSubcommand(plugin));
        addSubCommand(new ReloadSubcommand(plugin));
    }
    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "help-title", new StringReplacer("%command%", "/playerhunt"));
        Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "commands.help");
        return true;
    }
}
