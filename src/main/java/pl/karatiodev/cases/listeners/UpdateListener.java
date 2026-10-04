package pl.karatiodev.cases.listeners;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.utilities.MessageUtility;

@AllArgsConstructor
public class UpdateListener implements Listener {

    private final CasePlugin plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(!player.hasPermission("karatiocase.update")) return;

        if(!plugin.getUpdateChecker().isChecked()) return;
        if(!plugin.getUpdateChecker().isUpdateAvailable()) return;

        String currentVersion = plugin.getDescription().getVersion();
        String latestVersion = plugin.getUpdateChecker().getLatestVersion();

        player.sendMessage(MessageUtility.deserialize("<gray>[<aqua>KaratioCase<gray>] <red>Your plugin is outdated!"));
        player.sendMessage(MessageUtility.deserialize("<gray>Current version: <red>" + currentVersion));
        player.sendMessage(MessageUtility.deserialize("<gray>Latest version: <green>" + latestVersion));
        player.sendMessage(MessageUtility.deserialize("<yellow>Download the latest version from Github or Modrinth!"));
    }
}
