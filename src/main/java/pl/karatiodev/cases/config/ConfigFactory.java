package pl.karatiodev.cases.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigFactory {

    public static <T extends OkaeriConfig> T load(JavaPlugin plugin, Class<T> type, String fileName){
        return ConfigManager.create(type, config -> {
            config.withConfigurer(new YamlSnakeYamlConfigurer());
            config.withSerdesPack(new SerdesBukkit());
            config.withBindFile(new File(plugin.getDataFolder(), fileName));
            config.saveDefaults();
            config.load(true);
            config.save();
        });
    }
}
