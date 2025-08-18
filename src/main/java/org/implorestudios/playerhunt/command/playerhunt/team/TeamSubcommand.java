package org.implorestudios.playerhunt.command.playerhunt.team;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.command.playerhunt.team.add.AddSubcommand;
import org.implorestudios.playerhunt.game.Game;
import org.implorestudios.playerhunt.language.StringReplacer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TeamSubcommand extends Command {
    public TeamSubcommand(@NonNull PlayerHuntPlugin plugin) {
        super(plugin, "team");
        addSubCommand(new AddSubcommand(plugin));
        addSubCommand(new RemoveSubcommand(plugin));
        addSubCommand(new ViewSubcommand(plugin));
        addSubCommand(new ClearSubcommand(plugin));
        addSubCommand(new LastSubcommand(plugin));
    }

    @Override
    protected List<String> onTabComplete(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "helpHeader", new StringReplacer("%command%", "/playerhunt team"));
        Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "commands.team.help");
        return true;
    }
}
