package pl.karatiodev.cases.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseLocation;
import pl.karatiodev.cases.cases.CaseReward;
import pl.karatiodev.cases.utilities.CaseItemUtility;

import java.util.*;

@RequiredArgsConstructor
public class CaseManager {

    private final CasePlugin plugin;
    private final Map<String, CaseData> cases = new LinkedHashMap<>();

    @Getter
    private final RewardManager rewardManager = new RewardManager();

    public void load(){
        cases.clear();

        Map<String, CaseData> configuredCases = plugin.getConfigs().getCasesConfig().getCases();
        if(configuredCases == null) return;

        for(Map.Entry<String, CaseData> entry : configuredCases.entrySet()){
            String id = normalizeId(entry.getKey());

            CaseData data = entry.getValue();

            if(data == null) continue;
            data.setId(id);

            if(data.getRewards() == null) data.setRewards(new ArrayList<>());

            cases.put(id, data);
        }
    }

    public void save(){
        Map<String, CaseData> configCases = plugin.getConfigs().getCasesConfig().getCases();
        configCases.clear();

        for(Map.Entry<String, CaseData> entry : cases.entrySet()){
            configCases.put(entry.getKey(), entry.getValue());
        }

        plugin.getConfigs().getCasesConfig().save();
    }

    public CaseData create(String id, String displayName, Material blockType, Location location){
        String normalizedId = normalizeId(id);

        if(cases.containsKey(normalizedId)) return null;

        CaseData caseData = new CaseData();

        caseData.setId(normalizedId);
        caseData.setDisplayName(displayName);
        caseData.setBlockType(blockType);
        caseData.setLocation(new CaseLocation(location));
        caseData.setRewards(new ArrayList<>());

        cases.put(normalizedId, caseData);

        save();

        return caseData;
    }

    public boolean delete(String id){
        String normalizedId = normalizeId(id);

        if(!cases.containsKey(normalizedId)) return false;
        cases.remove(normalizedId);

        save();

        return true;
    }

    public boolean exists(String id){
        return id != null && cases.containsKey(normalizeId(id));
    }

    public CaseData get(String id){
        if(id == null) return null;

        return cases.get(normalizeId(id));
    }

    public Collection<CaseData> getCases(){
        return cases.values();
    }

    public CaseData findByLocation(Location location){
        if(location == null) return null;

        for(CaseData caseData : cases.values()){
            CaseLocation caseLocation = caseData.getLocation();
            if(caseLocation == null) continue;
            if(caseLocation.matches(location)) return caseData;
        }

        return null;
    }

    public CaseData findByBlock(Block block){
        if(block == null) return null;

        return findByLocation(block.getLocation());
    }

    public boolean move(String id, Location newLocation){
        CaseData caseData = get(id);
        if(caseData == null || newLocation == null) return false;

        caseData.setLocation(new CaseLocation(newLocation));

        save();
        return true;
    }

    public CaseReward rollReward(String id){
        CaseData caseData = get(id);
        if(caseData == null) return null;

        return rewardManager.roll(caseData);
    }

    public CaseReward rollReward(CaseData caseData){
        return rewardManager.roll(caseData);
    }

    public ItemStack createRewardDisplay(CaseReward reward){
        if(reward == null || reward.getItem() == null) return null;

        return CaseItemUtility.createDisplayReward(reward.getItem(), reward.getChance());
    }

    public ItemStack createKey(String id){
        CaseData caseData = get(id);
        if(caseData == null) return null;

        return CaseItemUtility.createKey(caseData);
    }

    public boolean isCorrectKey(ItemStack item, CaseData caseData){
        return CaseItemUtility.isKeyForCase(item, caseData);
    }

    public boolean consumeKey(Player player, CaseData caseData){
        if(player == null || caseData == null) return false;

        for(ItemStack item : player.getInventory().getContents()){
            if(!isCorrectKey(item, caseData)) continue;

            CaseItemUtility.consumeOne(item);

            player.updateInventory();
            return true;
        }

        return false;
    }

    public boolean hasKey(Player player, CaseData caseData){
        if(player == null || caseData == null) return false;

        for(ItemStack item : player.getInventory().getContents()){
            if(isCorrectKey(item, caseData)) return true;
        }

        return false;
    }

    private String normalizeId(String id){
        if(id == null) return "";

        return id.trim().toLowerCase(Locale.ROOT);
    }
}
