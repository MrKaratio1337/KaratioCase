package pl.karatiodev.cases;

import dev.rollczi.litecommands.LiteCommands;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pl.karatiodev.cases.config.Configs;
import pl.karatiodev.cases.manager.CaseManager;

public class CasePlugin extends JavaPlugin {

    @Getter
    private static CasePlugin instance;

    @Getter
    private Configs configs;

    private LiteCommands<CommandSender> liteCommands;

    @Getter
    private CaseManager caseManager;

    @Override
    public void onEnable() {
        instance = this;

        this.configs = new Configs(this);

        this.caseManager = new CaseManager(this);

        getLogger().info("KaratioCase enabled. Loaded " + caseManager.getCases().size() + " cases.");
    }

    @Override
    public void onDisable() {
        if(liteCommands != null) liteCommands.unregister();
        if(caseManager != null) caseManager.save();

        instance = null;
    }
}
