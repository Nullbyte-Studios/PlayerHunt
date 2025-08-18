package org.implorestudios.playerhunt.language;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LanguageManager {
    private final JavaPlugin plugin;
    private final Logger logger;
    private final Map<String, YamlConfiguration> languageFiles;
    private YamlConfiguration languageConfig;
    private String defaultLocale;
    private String consoleLocale;
    private boolean useDefaultLocale;
    
    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.languageFiles = new HashMap<>();
        this.defaultLocale = "en_us";
        this.consoleLocale = "en_us";
        this.useDefaultLocale = false;
    }
    
    public void saveDefaultLanguageFiles() {
        // Save language.yml
        saveLanguageFile("language.yml");
        
        // Save default language file
        File langDir = new File(plugin.getDataFolder(), "language");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }
        saveLanguageFile("language/en_us.lang.yml");
    }
    
    private void saveLanguageFile(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }
                
                InputStream inputStream = plugin.getResource(fileName);
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath());
                    logger.info("Saved default " + fileName);
                } else {
                    logger.warning("Could not find " + fileName + " in resources!");
                }
            } catch (IOException e) {
                logger.severe("Could not save " + fileName + ": " + e.getMessage());
            }
        }
    }
    
    public void reloadLanguages() {
        // Load language.yml configuration
        File languageConfigFile = new File(plugin.getDataFolder(), "language.yml");
        if (languageConfigFile.exists()) {
            languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);
            useDefaultLocale = languageConfig.getBoolean("enforce-default-locale", false);
            defaultLocale = languageConfig.getString("default-locale", "en_us");
            consoleLocale = languageConfig.getString("console-locale", "en_us");
        }
        
        // Load language files from language directory
        File langDir = new File(plugin.getDataFolder(), "language");
        if (langDir.exists() && langDir.isDirectory()) {
            File[] files = langDir.listFiles((dir, name) -> name.endsWith(".lang.yml"));
            if (files != null) {
                for (File file : files) {
                    String locale = file.getName().replace(".lang.yml", "");
                    YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(file);
                    languageFiles.put(locale, langConfig);
                }
            }
        }
        
        logger.info("Loaded " + languageFiles.size() + " language files");
    }
    
    public void onPluginEnable() {
        // Plugin-specific initialization if needed
    }
    
    private String getPlayerLocale(CommandSender sender) {
        if (useDefaultLocale || !(sender instanceof Player)) {
            return defaultLocale;
        }
        
        if (sender instanceof Player player) {
            String locale = player.locale().toString().toLowerCase().replace("-", "_");
            return languageFiles.containsKey(locale) ? locale : defaultLocale;
        }
        
        return consoleLocale;
    }
    
    private YamlConfiguration getLanguageFile(String locale) {
        return languageFiles.getOrDefault(locale, languageFiles.get(defaultLocale));
    }
    
    public String getMessage(CommandSender sender, String key, Replacer... replacers) {
        String locale = getPlayerLocale(sender);
        YamlConfiguration langFile = getLanguageFile(locale);
        
        if (langFile == null) {
            return "Missing language file for " + locale;
        }
        
        String message = langFile.getString(key);
        if (message == null) {
            return "Missing key: " + key;
        }
        
        // Apply replacers
        for (Replacer replacer : replacers) {
            message = replacer.replace(message);
        }
        
        return message;
    }
    
    public String getMessageWithPrefix(CommandSender sender, String key, Replacer... replacers) {
        String locale = getPlayerLocale(sender);
        YamlConfiguration langFile = getLanguageFile(locale);
        
        if (langFile == null) {
            return "Missing language file for " + locale;
        }
        
        String prefix = langFile.getString("prefix", "");
        String message = getMessage(sender, key, replacers);
        
        return prefix.isEmpty() ? message : prefix + " " + message;
    }
    
    public void sendMessage(CommandSender sender, String key, Replacer... replacers) {
        String message = getMessage(sender, key, replacers);
        Component component = MiniMessage.miniMessage().deserialize(message);
        sender.sendMessage(component);
    }
    
    public void sendMessageWithPrefix(CommandSender sender, String key, Replacer... replacers) {
        String message = getMessageWithPrefix(sender, key, replacers);
        Component component = MiniMessage.miniMessage().deserialize(message);
        sender.sendMessage(component);
    }
    
    public void sendModifiableMessageWithPrefix(CommandSender sender, String key, Replacer... replacers) {
        String locale = getPlayerLocale(sender);
        YamlConfiguration langFile = getLanguageFile(locale);
        
        if (langFile == null) {
            sender.sendMessage("Missing language file for " + locale);
            return;
        }
        
        String messageType = langFile.getString(key + ".type", "CHAT");
        String content = langFile.getString(key + ".content", "");
        
        // Apply replacers
        for (Replacer replacer : replacers) {
            content = replacer.replace(content);
        }
        
        if ("ACTION_BAR".equals(messageType) && sender instanceof Player player) {
            Component component = MiniMessage.miniMessage().deserialize(content);
            player.sendActionBar(component);
        } else {
            String prefix = langFile.getString("prefix", "");
            String fullMessage = prefix.isEmpty() ? content : prefix + " " + content;
            Component component = MiniMessage.miniMessage().deserialize(fullMessage);
            sender.sendMessage(component);
        }
    }
    
    public void sendTitle(Player player, String key, Replacer... replacers) {
        String locale = getPlayerLocale(player);
        YamlConfiguration langFile = getLanguageFile(locale);
        
        if (langFile == null) {
            return;
        }
        
        String title = langFile.getString(key + ".title", "");
        String subtitle = langFile.getString(key + ".subtitle", "");
        int fadeIn = langFile.getInt(key + ".fade-in", 10);
        int stay = langFile.getInt(key + ".stay", 70);
        int fadeOut = langFile.getInt(key + ".fade-out", 20);
        
        // Apply replacers
        for (Replacer replacer : replacers) {
            title = replacer.replace(title);
            subtitle = replacer.replace(subtitle);
        }
        
        Component titleComponent = MiniMessage.miniMessage().deserialize(title);
        Component subtitleComponent = MiniMessage.miniMessage().deserialize(subtitle);
        
        Title titleObject = Title.title(
            titleComponent,
            subtitleComponent,
            Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),  // Convert ticks to millis
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
            )
        );
        
        player.showTitle(titleObject);
    }
}