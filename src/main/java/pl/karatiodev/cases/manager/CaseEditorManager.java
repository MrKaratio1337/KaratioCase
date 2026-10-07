package pl.karatiodev.cases.manager;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseReward;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CaseEditorManager {

    private final CasePlugin plugin;

    private final Map<UUID, CaseEditorSession> sessions = new ConcurrentHashMap<>();

    public boolean isEditing(Player player) {
        return player != null && sessions.containsKey(player.getUniqueId());
    }

    public CaseEditorSession getSession(Player player){
        if(player == null) return null;

        return sessions.get(player.getUniqueId());
    }

    public void start(Player player, CaseData caseData){
        if(player == null || caseData == null) return;

        if(isEditing(player)) return;

        CaseEditorSession session = new CaseEditorSession(player.getUniqueId(), caseData.getId(), caseData.getRewards());
        sessions.put(player.getUniqueId(), session);
    }

    public void remove(Player player){
        if(player == null) return;

        sessions.remove(player.getUniqueId());
    }

    public boolean save(Player player, Inventory inventory){
        if(player == null || inventory == null) return false;

        CaseEditorSession session = sessions.get(player.getUniqueId());
        if(session == null) return false;

        CaseData caseData = plugin.getCaseManager().get(session.getCaseId());
        if(caseData == null) return false;

        List<CaseReward> rewards = new ArrayList<>();
        for(int slot : getRewardSlots()){
            ItemStack item = inventory.getItem(slot);
            if(item == null || item.getType().isAir()) continue;

            CaseReward reward = new CaseReward();
            reward.setItem(item.clone());

            reward.setChance(1.0D);
            rewards.add(reward);
        }

        caseData.setRewards(rewards);
        plugin.getCaseManager().save();

        session.setSaved(true);
        sessions.remove(player.getUniqueId());

        return true;
    }

    public List<ItemStack> getOriginalItems(Player player){
        CaseEditorSession session = getSession(player);
        if(session == null) return List.of();

        List<ItemStack> items = new ArrayList<>();
        for(ItemStack item : session.getOriginalItems()){
            if(item != null) items.add(item.clone());
        }

        return items;
    }

    public List<ItemStack> getEditorItems(Inventory inventory){
        List<ItemStack> items = new ArrayList<>();
        if(inventory == null) return items;

        for(int slot : getRewardSlots()){
            ItemStack item = inventory.getItem(slot);
            if(item == null || item.getType().isAir()) continue;

            items.add(item.clone());
        }

        return items;
    }

    public int[] getRewardSlots(){
        return new int[]{
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };
    }
}
