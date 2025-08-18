package org.implorestudios.playerhunt.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigurationManager {
    private final JavaPlugin plugin;
    private final Map<String, YamlConfiguration> configurations;
    private final Logger logger;

    public ConfigurationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configurations = new HashMap<>();
        this.logger = plugin.getLogger();
    }

    /**
     * Saves the default configuration file from resources to the plugin folder
     */
    public void saveDefault(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                // Create directories if they don't exist
                if (!plugin.getDataFolder().exists()) {
                    plugin.getDataFolder().mkdirs();
                }
                
                // Save the resource file
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

    /**
     * Reloads a configuration file from disk
     */
    public void reload(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            configurations.put(fileName, config);
            logger.info("Reloaded " + fileName);
        } else {
            logger.warning("Could not reload " + fileName + " - file does not exist!");
        }
    }

    /**
     * Gets a loaded configuration file
     */
    public YamlConfiguration get(String fileName) {
        return configurations.get(fileName);
    }

    /**
     * Saves a configuration file to disk
     */
    public void save(String fileName) {
        YamlConfiguration config = configurations.get(fileName);
        if (config != null) {
            try {
                File file = new File(plugin.getDataFolder(), fileName);
                config.save(file);
                logger.info("Saved " + fileName);
            } catch (IOException e) {
                logger.severe("Could not save " + fileName + ": " + e.getMessage());
            }
        } else {
            logger.warning("Could not save " + fileName + " - configuration not loaded!");
        }
    }
}