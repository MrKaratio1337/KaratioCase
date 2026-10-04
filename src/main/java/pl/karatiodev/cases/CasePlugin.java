package pl.karatiodev.cases;

import dev.rollczi.litecommands.LiteCommands;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.karatiodev.cases.config.Configs;

@Getter
public class CasePlugin extends JavaPlugin {

    private Configs configs;
    private LiteCommands<CommandSender> liteCommands;

    @Override
    public void onEnable() {
        this.configs = new Configs(this);

        getLogger().info("KaratioCase enabled");
    }

    @Override
    public void onDisable() {
        if(liteCommands != null) liteCommands.unregister();
    }
}
