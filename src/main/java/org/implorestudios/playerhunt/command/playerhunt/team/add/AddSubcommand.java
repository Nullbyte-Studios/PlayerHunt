package org.implorestudios.playerhunt.command.playerhunt.team.add;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.command.Command;
import org.implorestudios.playerhunt.language.StringReplacer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AddSubcommand extends Command {
    public AddSubcommand(@NonNull PlayerHuntPlugin plugin) {
        super(plugin, "add");
        addSubCommand(new HunterAddSubcommand(plugin));
        addSubCommand(new RunnerAddSubcommand(plugin));
    }
    @Override
    protected List<String> onTabComplete(@NonNull CommandSender sender, String @NonNull [] args) {
        return Collections.emptyList();
    }
    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "helpHeader", new StringReplacer("%command%", "/playerhunt add"));
        Objects.requireNonNull(getLanguageManager()).sendMessage(commandSender, "commands.team.add.help");
        return true;
    }

}
