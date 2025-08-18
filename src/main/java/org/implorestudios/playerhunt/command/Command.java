package org.implorestudios.playerhunt.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.implorestudios.playerhunt.language.LanguageManager;
import org.implorestudios.playerhunt.language.Replacer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final String name;
    private final Map<String, Command> subCommands;
    private String permissionName;
    
    protected Command(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.subCommands = new HashMap<>();
    }
    
    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }
    
    public void addSubCommand(Command subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }
    
    public String getName() {
        return name;
    }
    
    protected JavaPlugin getPlugin() {
        return plugin;
    }
    
    protected LanguageManager getLanguageManager() {
        if (plugin instanceof org.implorestudios.playerhunt.PlayerHuntPlugin huntPlugin) {
            return huntPlugin.getLanguageManager();
        }
        return null;
    }
    
    public void register() {
        org.bukkit.command.PluginCommand command = plugin.getCommand(name);
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        // Check permission
        if (permissionName != null && !sender.hasPermission(permissionName)) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        // Handle subcommands
        if (args.length > 0 && subCommands.containsKey(args[0])) {
            Command subCommand = subCommands.get(args[0]);
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCommand.execute(sender, newArgs);
        }
        
        // Execute the main command
        return execute(sender, args);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        // Check permission
        if (permissionName != null && !sender.hasPermission(permissionName)) {
            return new ArrayList<>();
        }
        
        // Handle subcommand tab completion
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String subCommandName : subCommands.keySet()) {
                if (subCommandName.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommandName);
                }
            }
            return completions;
        } else if (args.length > 1 && subCommands.containsKey(args[0])) {
            Command subCommand = subCommands.get(args[0]);
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCommand.onTabComplete(sender, newArgs);
        }
        
        return onTabComplete(sender, args);
    }
    
    protected abstract boolean execute(CommandSender sender, String[] args);
    protected abstract List<String> onTabComplete(CommandSender sender, String[] args);
    
    // Helper methods for sending messages
    protected void sendMessage(CommandSender sender, String key, Replacer... replacers) {
        LanguageManager languageManager = getLanguageManager();
        if (languageManager != null) {
            languageManager.sendMessage(sender, key, replacers);
        }
    }
    
    protected void sendMessageWithPrefix(CommandSender sender, String key, Replacer... replacers) {
        LanguageManager languageManager = getLanguageManager();
        if (languageManager != null) {
            languageManager.sendMessageWithPrefix(sender, key, replacers);
        }
    }
}