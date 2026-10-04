package pl.karatiodev.cases.config;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Configs extends OkaeriConfig {

    private final PluginConfig pluginConfig;
    private final CasesConfig casesConfig;
    private final MessagesConfig messagesConfig;

    public Configs(JavaPlugin plugin){
        this.pluginConfig = ConfigFactory.load(plugin, PluginConfig.class, "config.yml");
        this.casesConfig = ConfigFactory.load(plugin, CasesConfig.class, "cases.yml");
        this.messagesConfig = ConfigFactory.load(plugin, MessagesConfig.class, "messages.yml");
    }

    public void reload(JavaPlugin plugin){
        pluginConfig.load(true);
        casesConfig.load(true);
        messagesConfig.load(true);
    }
}
