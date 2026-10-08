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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseReward;
import pl.karatiodev.cases.inventories.AnimationHolder;
import pl.karatiodev.cases.inventories.CaseEditorHolder;
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

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) return;

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

        if(event.getView().getTopInventory().getHolder() instanceof CaseEditorHolder){
            handleEditorClick(event, player);
        }

        if(event.getView().getTopInventory().getHolder() instanceof PreviewHolder previewHolder) {
            handlePreviewClick(event, player, previewHolder.getCaseData());
        }
    }

    private void handleEditorClick(InventoryClickEvent event, Player player){
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
            return;
        }

        int slot = event.getRawSlot();

        if(slot < 0) return;
        if(slot >= event.getView().getTopInventory().getSize()) return;

        if(slot == 45){
            event.setCancelled(true);

            boolean saved = plugin.getCaseEditorManager().save(player, event.getView().getTopInventory());
            if(!saved) return;

            player.closeInventory();
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getRewardSaved()));
            return;
        }

        if(slot == 49){
            event.setCancelled(true);

            cancelEditor(player, event.getView().getTopInventory());
            return;
        }

        if(slot == 53){
            event.setCancelled(true);

            clearEditorRewards(event.getView().getTopInventory());
            return;
        }

        if (!isRewardSlot(slot)) {
            event.setCancelled(true);
        }
    }

    private void clearEditorRewards(Inventory inventory){
        for (int slot : plugin.getCaseEditorManager().getRewardSlots()) {
            inventory.setItem(slot, null);
        }
    }

    private boolean isRewardSlot(int slot){
        for(int rewardSlot : plugin.getCaseEditorManager().getRewardSlots()){
            if(rewardSlot == slot) return true;
        }

        return false;
    }

    private void cancelEditor(Player player, Inventory inventory){
        plugin.getCaseEditorManager().remove(player);

        player.closeInventory();

        player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getChangesCancelled()));
    }

    private void handleAnimationClick(InventoryClickEvent event){
        event.setCancelled(true);
    }

    private void handlePreviewClick(InventoryClickEvent event, Player player, CaseData caseData){
        event.setCancelled(true);

        if(caseData == null){
            player.closeInventory();
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

        if(!(event.getView().getTopInventory().getHolder() instanceof CaseEditorHolder)) return;

        for(int rawSlot : event.getRawSlots()) {
            if (rawSlot >= event.getView().getTopInventory().getSize()) continue;

            if (!isRewardSlot(rawSlot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryCreative(InventoryCreativeEvent event){
        if(event.getView().getTopInventory().getHolder() instanceof PreviewHolder
                || event.getView().getTopInventory().getHolder() instanceof AnimationHolder
        || event.getView().getTopInventory().getHolder() instanceof CaseEditorHolder){
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

    @EventHandler
    public void onEditorClose(InventoryCloseEvent event){
        if(!(event.getPlayer() instanceof Player player)) return;
        if(!(event.getInventory().getHolder() instanceof CaseEditorHolder)) return;
        if(!plugin.getCaseEditorManager().isEditing(player)) return;

        List<ItemStack> items = plugin.getCaseEditorManager().getEditorItems(event.getInventory());
        plugin.getCaseEditorManager().remove(player);

        for(ItemStack item : items){
            if(item == null || item.getType().isAir())continue;

            var leftovers = player.getInventory().addItem(item);

            for(ItemStack leftover : leftovers.values()){
                if(leftover == null || leftover.getType().isAir()) continue;

                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnimationClose(InventoryCloseEvent event){
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

        if(plugin.getCaseEditorManager().isEditing(event.getPlayer())) plugin.getCaseEditorManager().remove(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getAnimationManager().deliverPendingReward(player));
    }

    private void startAnimated(Player player, CaseData caseData){
        var animation = plugin.getConfigs().getPluginConfig().getAnimation();

        if(!animation.isEnabled()){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getAnimationDisabled()));
            return;
        }

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
