package org.implorestudios.playerhunt.command.playerhunt;

import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.command.Command;

import java.util.List;

public class ReloadSubcommand extends Command {
    private final PlayerHuntPlugin plugin;
    public ReloadSubcommand(@NonNull PlayerHuntPlugin plugin) {
        super(plugin, "reload");
        this.plugin = plugin;
    }

    @Override
    protected List<String> onTabComplete(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        return List.of();
    }

    @Override
    protected boolean execute(@NonNull CommandSender commandSender, String @NonNull [] strings) {
        plugin.reload();
        sendMessageWithPrefix(commandSender, "commands.reload");
        return false;
    }
}
