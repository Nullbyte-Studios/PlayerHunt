package org.implorestudios.playerhunt;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.implorestudios.playerhunt.command.playerhunt.PlayerhuntCommand;
import org.implorestudios.playerhunt.config.ConfigurationManager;
import org.implorestudios.playerhunt.event.GameEvents;
import org.implorestudios.playerhunt.language.LanguageManager;
import org.implorestudios.playerhunt.language.Replacer;

@Getter
public final class PlayerHuntPlugin extends JavaPlugin {
    public static PlayerHuntPlugin plugin;
    private ConfigurationManager configurationManager;
    private LanguageManager languageManager;
    private YamlConfiguration mainConfig;
    
    public static Component mmString(String string) {
        String text = MiniMessage.miniMessage().serialize(MiniMessage.miniMessage().deserialize(string));
        return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text);
    }
    
    public static Component mm(CommandSender audience, String key) {
        String text = plugin.getLanguageManager().getMessageWithPrefix(audience, key);
        return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text);
    }
    
    public static Component mm(CommandSender audience, String key, Replacer... replacer) {
        String text = plugin.getLanguageManager().getMessageWithPrefix(audience, key, replacer);
        return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(text);
    }
    @Override
    public void onLoad() {
        this.loadConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        this.configurationManager = new ConfigurationManager(this);
        this.languageManager = new LanguageManager(this);
        this.loadLanguages();
        this.registerCommands();
        this.registerListeners();
        getLogger().info("PlayerHunt plugin has been enabled.");
    }

    public void reload() {
        this.loadConfig();
        this.loadLanguages();
        this.registerCommands();
        this.registerListeners();
        getLogger().info("PlayerHunt plugin has been reloaded.");
    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerHunt plugin has been disabled.");
    }

    private void loadConfig() {
        configurationManager.saveDefault("config.yml");
        configurationManager.reload("config.yml");
        this.mainConfig = configurationManager.get("config.yml");
        getLogger().info("Configuration loaded");
    }

    private void loadLanguages() {
        languageManager.saveDefaultLanguageFiles();
        languageManager.reloadLanguages();
        languageManager.onPluginEnable();
        getLogger().info("Languages loaded");
    }

    private void registerCommands() {
        new PlayerhuntCommand(this).register();
        getLogger().info("Commands registered");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GameEvents(), this);
        getLogger().info("Events registered");
    }
}
