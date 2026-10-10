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

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class AnimationManager {

    private static final int INVENTORY_SIZE = 27;

    private static final int REWARD_START_SLOT = 9;
    private static final int REWARD_END_SLOT = 17;
    private static final int WINNER_SLOT = 13;

    private final CasePlugin plugin;

    private final Map<UUID, AnimationSession> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, ItemStack> pendingRewards = new ConcurrentHashMap<>();

    public boolean isRunning(Player player){
        return player != null && sessions.containsKey(player.getUniqueId());
    }

    public boolean isRunning(UUID uuid){
        return uuid != null && sessions.containsKey(uuid);
    }

    public void start(Player player, CaseData caseData, CaseReward reward){
        if(player == null || caseData == null || reward == null) return;

        if(isRunning(player)) return;

        if(reward.getItem() == null || reward.getItem().getType().isAir()) return;

        List<CaseReward> validRewards = plugin.getCaseManager().getRewardManager().getValidRewards(caseData);
        if(validRewards.isEmpty()) return;

        var guiConfig = plugin.getConfigs().getPluginConfig().getGui();

        int inventorySize = normalizeInventorySize(guiConfig.getAnimationSize());
        String title = guiConfig.getAnimationTitle().replace("%case%", caseData.getDisplayName());

        AnimationHolder holder = new AnimationHolder(caseData);

        Inventory inventory = Bukkit.createInventory(holder, inventorySize, MessageUtility.deserialize(title));
        holder.setInventory(inventory);

        fillAnimationBackground(inventory);

        for(int slot = REWARD_START_SLOT; slot <= REWARD_END_SLOT; slot++){
            inventory.setItem(slot, createRandomDisplayItem(validRewards));
        }

        AnimationSession session = new AnimationSession(player.getUniqueId(), caseData, reward, inventory, validRewards);
        sessions.put(player.getUniqueId(), session);

        player.openInventory(inventory);

        schedule(session);
    }

    private void schedule(AnimationSession session){
        var animation = plugin.getConfigs().getPluginConfig().getAnimation();

        int steps = Math.max(5, animation.getSteps());
        long startDelay = Math.max(1L, animation.getStartDelay());
        long maxDelay = Math.max(startDelay, animation.getMaxDelay());

        scheduleNextStep(session, steps, startDelay, maxDelay);
    }

    private void scheduleNextStep(AnimationSession session, int steps, long startDelay, long maxDelay){
        if(session.isFinished()) return;

        long delay = calculateDelay(session.getStep(), steps, startDelay, maxDelay);

        session.setBukkitTask(Bukkit.getScheduler().runTaskLater(plugin, () -> executeStep(session, steps, startDelay, maxDelay), delay));
    }

    private void executeStep(AnimationSession session, int steps, long startDelay, long maxDelay){
        if(session.isFinished()) return;

        Player player = Bukkit.getPlayer(session.getPlayerId());
        if(player == null || !player.isOnline()){
            finishOffline(session);
            return;
        }

        if(player.getOpenInventory().getTopInventory() != session.getInventory()){
            if(plugin.getConfigs().getPluginConfig().getAnimation().isCloseProtection()){
                player.openInventory(session.getInventory());
            } else{
                finish(session, player);
                return;
            }
        }

        moveAnimationItems(session);

        int currentStep = session.getStep();

        boolean insertWinner = currentStep == steps - 5;
        if(insertWinner) session.getInventory().setItem(REWARD_END_SLOT, session.getReward().getItem().clone());
        else session.getInventory().setItem(REWARD_END_SLOT, createRandomDisplayItem(session.getValidRewards()));

        session.setStep(currentStep + 1);
        boolean lastStep = session.getStep() >= steps;
        if(lastStep){
            finish(session, player);
            return;
        }

        playRollingSound(player);

        scheduleNextStep(session, steps, startDelay, maxDelay);
    }

    private void moveAnimationItems(AnimationSession session){
        Inventory inventory = session.getInventory();

        for (int slot = REWARD_START_SLOT; slot < REWARD_END_SLOT; slot++) {
            inventory.setItem(slot, inventory.getItem(slot + 1));
        }
    }

    private void finish(AnimationSession session, Player player){
        if(session.isFinished()) return;
        session.setFinished(true);

        cancelTask(session);

        ItemStack reward = session.getReward().getItem();
        if(reward == null || reward.getType().isAir()){
            sessions.remove(player.getUniqueId());
            return;
        }

        if(!session.isRewarded()){
            session.setRewarded(true);

            giveReward(player, reward);
            playRewardSound(player);
        }

        sessions.remove(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(!player.isOnline()) return;

           if(player.getOpenInventory().getTopInventory() == session.getInventory()){
               player.closeInventory();
           }
        }, 40);
    }

    private void finishOffline(AnimationSession session){
        if(session.isFinished()) return;

        session.setFinished(true);

        cancelTask(session);

        sessions.remove(session.getPlayerId());

        if(!session.isRewarded()){
            session.setRewarded(true);

            pendingRewards.put(session.getPlayerId(), session.getReward().getItem().clone());
        }
    }

    public void handleQuit(Player player){
        if(player == null) return;

        UUID uuid = player.getUniqueId();

        AnimationSession session = sessions.remove(uuid);
        if(session == null) return;

        session.setFinished(true);

        cancelTask(session);

        if(!session.isRewarded()){
            session.setRewarded(true);

            pendingRewards.put(uuid, session.getReward().getItem().clone());
        }
    }

    public boolean deliverPendingReward(Player player){
        if(player == null) return false;

        UUID uuid = player.getUniqueId();

        ItemStack reward = pendingRewards.remove(uuid);
        if(reward == null) return false;

        giveReward(player, reward);

        return true;
    }

    private void giveReward(Player player, ItemStack reward){
        if(player == null || reward == null) return;

        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(reward);
        if(leftovers.isEmpty()) return;

        for(ItemStack leftover : leftovers.values()){
            if(leftover == null || leftover.getType().isAir()) continue;

            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private void fillAnimationBackground(Inventory inventory){
        for(int i = 0; i < inventory.getSize(); i++){
            inventory.setItem(i, CaseInventoryItems.createFiller());
        }
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
        if(player == null || reward == null || reward.getItem() == null || reward.getItem().getType().isAir()) return;

        ItemStack item = reward.getItem().clone();

        giveReward(player, item);
    }

    private ItemStack createRandomDisplayItem(List<CaseReward> rewards){
        if(rewards == null || rewards.isEmpty()) return CaseInventoryItems.createFiller();

        CaseReward reward = rewards.get(ThreadLocalRandom.current().nextInt(rewards.size()));
        if(reward == null || reward.getItem() == null || reward.getItem().getType().isAir()) return CaseInventoryItems.createFiller();

        return reward.getItem().clone();
    }

    private long calculateDelay(int step, int steps, long startDelay, long maxDelay){
        if(step <= 1) return startDelay;

        double progress = Math.min(1.0D, Math.max(0.0D, (double) step / (double) (steps - 1)));
        double eased = progress * progress;
        double delay = startDelay + ((double) maxDelay - startDelay) * eased;

        return Math.max(1L, Math.round(delay));
    }

    private void cancelTask(AnimationSession session){
        if(session.getBukkitTask() == null) return;
        if(!session.getBukkitTask().isCancelled()) session.getBukkitTask().cancel();

        session.setBukkitTask(null);
    }

    private int normalizeInventorySize(int size){
        if (size < INVENTORY_SIZE) return INVENTORY_SIZE;
        if (size > 54) return 54;
        if (size % 9 != 0) return ((size / 9) + 1) * 9;

        return size;
    }

    public void shutdown(){
        for(AnimationSession session : sessions.values()){
            if(session == null) continue;

            cancelTask(session);

            Player player = Bukkit.getPlayer(session.getPlayerId());

            if(!session.isRewarded()){
                session.setRewarded(true);

                ItemStack reward = session.getReward().getItem().clone();

                if(player != null && player.isOnline()){
                    giveReward(player, reward);
                } else{
                    pendingRewards.put(session.getPlayerId(), reward);
                }
            }

            session.setFinished(true);
        }

        sessions.clear();
    }

    private void playRollingSound(Player player){
        var sounds = plugin.getConfigs().getPluginConfig().getSounds();
        if(!sounds.isEnabled()) return;

        player.playSound(player.getLocation(), sounds.getRollingSound(), sounds.getRollingVolume(), sounds.getRollingPitch());
    }

    private void playRewardSound(Player player){
        var sounds = plugin.getConfigs().getPluginConfig().getSounds();
        if(!sounds.isEnabled()) return;

        player.playSound(player.getLocation(), sounds.getRewardSound(), sounds.getRewardVolume(), sounds.getRewardPitch());
    }

    @Getter
    @RequiredArgsConstructor
    private static class AnimationSession {
        private final UUID playerId;
        private final CaseData caseData;
        private final CaseReward reward;
        private final Inventory inventory;
        private final List<CaseReward> validRewards;

        @Setter
        private int step;

        @Setter
        private boolean finished;

        @Setter
        private boolean rewarded;

        @Setter
        private BukkitTask bukkitTask;
    }
}
