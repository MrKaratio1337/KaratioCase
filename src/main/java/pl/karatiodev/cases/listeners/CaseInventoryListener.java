package pl.karatiodev.cases.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseReward;
import pl.karatiodev.cases.inventories.AnimationHolder;
import pl.karatiodev.cases.inventories.PreviewHolder;

@RequiredArgsConstructor
public class CaseInventoryListener implements Listener {

    private final CasePlugin plugin;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player player)) return;

        if(event.getView().getTopInventory().getHolder() instanceof AnimationHolder){
            event.setCancelled(true);
            return;
        }

        if(!(event.getView().getTopInventory().getHolder() instanceof PreviewHolder holder)) return;

        event.setCancelled(true);

        CaseData caseData = holder.getCaseData();

        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory() != event.getView().getTopInventory()) return;

        int slot = event.getRawSlot();

        var guiConfig = plugin.getConfigs().getPluginConfig().getGui();

        if(slot == guiConfig.getCloseButtonSlot()){
            player.closeInventory();
            return;
        }

        if(slot == guiConfig.getAnimatedButtonSlot()){
            startAnimated(player, caseData);
            return;
        }

        if(slot == guiConfig.getInstantButtonSlot()){
            startInstant(player, caseData);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event){
        if(event.getView().getTopInventory().getHolder() instanceof PreviewHolder
        || event.getView().getTopInventory().getHolder() instanceof AnimationHolder){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent event){
        if(!(event.getPlayer() instanceof Player player)) return;

        if(!(event.getInventory().getHolder() instanceof AnimationHolder)) return;

        var animation = plugin.getConfigs().getPluginConfig().getAnimation();
        if(!animation.isCloseProtection()) return;

        if(!plugin.getAnimationManager().isRunning(player)) return;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if(!player.isOnline()) return;

            if(!plugin.getAnimationManager().isRunning(player)) return;

            CaseData caseData = plugin.getAnimationManager().getCaseData(player);
            if(caseData != null){
                plugin.getAnimationManager().reopen(player);
            }
        });
    }

    private void startAnimated(Player player, CaseData caseData){
        if(plugin.getAnimationManager().isRunning(player)) return;
        if(!plugin.getCaseManager().hasKey(player, caseData)) return;

        CaseReward reward = plugin.getCaseManager().rollReward(caseData);
        if(reward == null) return;

        if(!plugin.getCaseManager().consumeKey(player, caseData)) return;

        player.closeInventory();

        plugin.getAnimationManager().start(player, caseData, reward);
    }

    private void startInstant(Player player, CaseData caseData){
        if(plugin.getAnimationManager().isRunning(player)) return;

        if(!plugin.getCaseManager().hasKey(player, caseData)) return;

        CaseReward reward = plugin.getCaseManager().rollReward(caseData);
        if(reward == null) return;

        if(!plugin.getCaseManager().consumeKey(player, caseData)) return;

        player.closeInventory();

        plugin.getAnimationManager().giveInstantReward(player, reward);
    }
}
