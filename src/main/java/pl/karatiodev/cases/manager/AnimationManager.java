package pl.karatiodev.cases.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseReward;
import pl.karatiodev.cases.inventories.AnimationHolder;
import pl.karatiodev.cases.inventories.CaseInventoryItems;
import pl.karatiodev.cases.utilities.MessageUtility;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class AnimationManager {

    private final CasePlugin plugin;

    private final Map<UUID, AnimationSession> sessions = new ConcurrentHashMap<>();

    public boolean isRunning(Player player){
        return player != null && sessions.containsKey(player.getUniqueId());
    }

    public void start(Player player, CaseData caseData, CaseReward reward){
        if(player == null || caseData == null || reward == null) return;

        if(isRunning(player)) return;

        var config = plugin.getConfigs().getPluginConfig().getGui();

        String title = config.getAnimationTitle().replace("%case%", caseData.getDisplayName());

        AnimationHolder holder = new AnimationHolder(caseData);

        Inventory inventory = Bukkit.createInventory(holder, 27, MessageUtility.deserialize(title));
        holder.setInventory(inventory);

        fillAnimationBackground(inventory);
        player.openInventory(inventory);

        AnimationSession session = new AnimationSession(player.getUniqueId(), caseData, reward, inventory);
        sessions.put(player.getUniqueId(), session);

        schedule(session);
    }

    private void schedule(AnimationSession session){
        var animation = plugin.getConfigs().getPluginConfig().getAnimation();

        int steps = Math.max(1, animation.getSteps());
        long startDelay = Math.max(1, animation.getStartDelay());
        long maxDelay = Math.max(startDelay, animation.getMaxDelay());

        session.setBukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Player player = Bukkit.getPlayer(session.getPlayerId());
            if(player == null){
                finishOffline(session);
                return;
            }

            if(!player.isOnline()){
                finishOffline(session);
                return;
            }

            if(session.getStep() >= steps){
                finish(session, player);
                return;
            }

            animateStep(session, player, steps, startDelay, maxDelay);
        }, startDelay, calculatePeriod(steps, startDelay, maxDelay)));
    }

    private void animateStep(AnimationSession session, Player player, int steps, long startDelay, long maxDelay){
        Inventory inventory = session.getInventory();

        int currentSlot = 9 + (session.getStep() % 9);
        int previousSlot = 9 +((session.getStep() - 1) % 9);

        if(session.getStep() > 0){
            inventory.setItem(previousSlot, CaseInventoryItems.createFiller());
        }

        ItemStack display = session.getReward().getItem().clone();
        inventory.setItem(currentSlot, display);

        session.setStep(session.getStep() + 1);
    }

    private void finish(AnimationSession session, Player player){
        if(session.isFinished()) return;
        session.setFinished(true);

        if(session.getBukkitTask() != null) session.getBukkitTask().cancel();

        ItemStack reward = session.getReward().getItem().clone();
        session.getInventory().setItem(13, reward);

        giveReward(player, reward);

        sessions.remove(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
           if(player.isOnline() && player.getOpenInventory().getTopInventory() == session.getInventory()){
               player.closeInventory();
           }
        }, 40);
    }

    private void finishOffline(AnimationSession session){
        if(session.isFinished()) return;

        session.setFinished(true);

        if(session.getBukkitTask() != null) session.getBukkitTask().cancel();
        sessions.remove(session.getPlayerId());
    }

    private void giveReward(Player player, ItemStack reward){
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(reward);

        for(ItemStack leftover : leftovers.values()){
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    public void cancel(Player player){
        if(player == null) return;

        AnimationSession session = sessions.remove(player.getUniqueId());
        if(session == null) return;

        if(session.getBukkitTask() != null) session.getBukkitTask().cancel();
    }

    private void fillAnimationBackground(Inventory inventory){
        for(int i = 0; i < inventory.getSize(); i++){
            inventory.setItem(i, CaseInventoryItems.createFiller());
        }
    }

    private long calculatePeriod(int steps, long startDelay, long maxDelay){
        if(steps <= 1) return startDelay;

        return Math.max(1, (startDelay + maxDelay) / 2);
    }

    public CaseData getCaseData(Player player){
        if(player == null) return null;

        AnimationSession session = sessions.get(player.getUniqueId());
        if(session == null) return null;

        return session.getCaseData();
    }

    public void reopen(Player player){
        if(player == null) return;

        AnimationSession session = sessions.get(player.getUniqueId());
        if(session == null) return;

        player.openInventory(session.getInventory());
    }

    public void giveInstantReward(Player player, CaseReward reward){
        if(player == null || reward == null || reward.getItem() == null) return;

        ItemStack item = reward.getItem().clone();

        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);

        for(ItemStack leftover : leftovers.values()){
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    @Getter
    @RequiredArgsConstructor
    private static class AnimationSession {
        private final UUID playerId;
        private final CaseData caseData;
        private final CaseReward reward;
        private final Inventory inventory;

        @Setter
        private int step;

        @Setter
        private boolean finished;

        @Setter
        private BukkitTask bukkitTask;
    }
}
