package pl.karatiodev.cases.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseReward;
import pl.karatiodev.cases.inventories.AnimationHolder;
import pl.karatiodev.cases.inventories.PreviewHolder;
import pl.karatiodev.cases.inventories.PreviewInventory;
import pl.karatiodev.cases.utilities.MessageUtility;

import java.util.List;

@RequiredArgsConstructor
public class CaseInventoryListener implements Listener {

    private final CasePlugin plugin;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCaseInteract(PlayerInteractEvent event){
        if(event.getHand() != EquipmentSlot.HAND) return;

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if(block == null) return;

        CaseData caseData = plugin.getCaseManager().findByBlock(block);
        if(caseData == null) return;

        if(caseData.getBlockType() != block.getType()) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if(plugin.getAnimationManager().isRunning(player)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getAnimationRunning()));
            return;
        }

        PreviewInventory.open(player, caseData);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player player)) return;

        if(event.getView().getTopInventory().getHolder() instanceof AnimationHolder){
            handleAnimationClick(event);
            return;
        }

        if(event.getView().getTopInventory().getHolder() instanceof PreviewHolder previewHolder) {
            handlePreviewClick(event, player, previewHolder.getCaseData());
        }
    }

    private void handleAnimationClick(InventoryClickEvent event){
        event.setCancelled(true);

        if(event.getClick() == ClickType.DOUBLE_CLICK
                || event.getClick() == ClickType.NUMBER_KEY
                || event.getClick() == ClickType.SHIFT_LEFT
                || event.getClick() == ClickType.SHIFT_RIGHT
                || event.getClick() == ClickType.SWAP_OFFHAND
                || event.getClick() == ClickType.CONTROL_DROP
                || event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || event.getAction() == InventoryAction.HOTBAR_SWAP
                || event.getAction() == InventoryAction.COLLECT_TO_CURSOR){
            event.setCancelled(true);
        }
    }

    private void handlePreviewClick(InventoryClickEvent event, Player player, CaseData caseData){
        event.setCancelled(true);

        if(caseData == null){
            player.closeInventory();

            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseNotFound().replace("%id%", caseData.getId())));
            return;
        }

        if(event.getClickedInventory() == null) return;
        if(event.getClickedInventory() != event.getView().getTopInventory()) return;

        var gui = plugin.getConfigs().getPluginConfig().getGui();

        int slot = event.getRawSlot();
        if(slot == gui.getAnimatedButtonSlot()){
            startAnimated(player, caseData);
            return;
        }

        if(slot == gui.getInstantButtonSlot()){
            startInstant(player, caseData);
            return;
        }

        if(slot == gui.getCloseButtonSlot()){
            player.closeInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event){
        if(event.getView().getTopInventory().getHolder() instanceof PreviewHolder
        || event.getView().getTopInventory().getHolder() instanceof AnimationHolder){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryCreative(InventoryCreativeEvent event){
        if(event.getView().getTopInventory().getHolder() instanceof PreviewHolder
                || event.getView().getTopInventory().getHolder() instanceof AnimationHolder){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryMove(InventoryMoveItemEvent event){
        if(event.getDestination().getHolder() instanceof AnimationHolder
        || event.getSource().getHolder() instanceof AnimationHolder) event.setCancelled(true);

        if(event.getDestination().getHolder() instanceof PreviewHolder
                || event.getSource().getHolder() instanceof PreviewHolder) event.setCancelled(true);
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

            plugin.getAnimationManager().reopen(player);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        plugin.getAnimationManager().handleQuit(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getAnimationManager().deliverPendingReward(player));
    }

    private void startAnimated(Player player, CaseData caseData){
        var animation = plugin.getConfigs().getPluginConfig().getAnimation();
        if(!animation.isEnabled()) return;

        if(plugin.getAnimationManager().isRunning(player)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getAnimationRunning()));
            return;
        }

        List<CaseReward> rewards = plugin.getCaseManager().getRewardManager().getValidRewards(caseData);
        if(rewards.isEmpty()){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseEmpty()));
            return;
        }

        if(!plugin.getCaseManager().hasKey(player, caseData)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getNoKey()));
            return;
        }

        CaseReward reward = plugin.getCaseManager().rollReward(caseData);
        if(reward == null || reward.getItem() == null || reward.getItem().getType().isAir()){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseEmpty()));
            return;
        }

        if(!plugin.getCaseManager().consumeKey(player, caseData)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getNoKey()));
            return;
        }

        player.closeInventory();
        plugin.getAnimationManager().start(player, caseData, reward);
    }

    private void startInstant(Player player, CaseData caseData){
        if(plugin.getAnimationManager().isRunning(player)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getAnimationRunning()));
            return;
        }

        List<CaseReward> rewards = plugin.getCaseManager().getRewardManager().getValidRewards(caseData);
        if(rewards.isEmpty()){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseEmpty()));
            return;
        }

        if(!plugin.getCaseManager().hasKey(player, caseData)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getNoKey()));
            return;
        }

        CaseReward reward = plugin.getCaseManager().rollReward(caseData);
        if(reward == null || reward.getItem() == null || reward.getItem().getType().isAir()){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseEmpty()));
            return;
        }

        if(!plugin.getCaseManager().consumeKey(player, caseData)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getNoKey()));
            return;
        }

        plugin.getAnimationManager().giveInstantReward(player, reward);
        player.closeInventory();
    }
}
