package pl.karatiodev.cases;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.karatiodev.cases.commands.CaseCommand;
import pl.karatiodev.cases.config.Configs;
import pl.karatiodev.cases.listeners.CaseInventoryListener;
import pl.karatiodev.cases.listeners.UpdateListener;
import pl.karatiodev.cases.manager.AnimationManager;
import pl.karatiodev.cases.manager.CaseManager;

public class CasePlugin extends JavaPlugin {

    @Getter
    private static CasePlugin instance;

    @Getter
    private Configs configs;

    private LiteCommands<CommandSender> liteCommands;

    @Getter
    private CaseManager caseManager;

    @Getter
    private AnimationManager animationManager;

    @Getter
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        instance = this;

        this.configs = new Configs(this);

        this.caseManager = new CaseManager(this);
        this.caseManager.load();

        this.animationManager = new AnimationManager(this);

        this.updateChecker = new UpdateChecker(this);
        updateChecker.checkForUpdates();

        this.registerListeners();

        this.liteCommands = LiteBukkitFactory
                .builder("karatiocase")
                .commands(
                        new CaseCommand(this)
                ).build();

        getLogger().info("KaratioCase enabled. Loaded " + caseManager.getCases().size() + " cases.");
    }

    @Override
    public void onDisable() {
        if(liteCommands != null) liteCommands.unregister();
        if(caseManager != null) caseManager.save();

        instance = null;
    }

    private void registerListeners(){
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new UpdateListener(this), this);
        pluginManager.registerEvents(new CaseInventoryListener(this), this);
    }
}
