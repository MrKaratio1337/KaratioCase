package pl.karatiodev.cases.manager;

import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseReward;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class RewardManager {

    public CaseReward roll(CaseData caseData){
        List<CaseReward> validRewards = getValidRewards(caseData);
        if(validRewards.isEmpty()) return null;

        double totalChance = validRewards.stream().mapToDouble(CaseReward::getChance).sum();
        if(!Double.isFinite(totalChance) || totalChance <= 0.0D) return null;

        double random = ThreadLocalRandom.current().nextDouble(totalChance);
        double current = 0.0D;

        for(CaseReward reward : validRewards){
            current += reward.getChance();

            if(random < current) return reward;
        }

        return validRewards.get(validRewards.size() - 1);
    }

    public List<CaseReward> getValidRewards(CaseData caseData){
        if(caseData == null || caseData.getRewards() == null) return List.of();

        List<CaseReward> rewards = new ArrayList<>();

        for(CaseReward reward : caseData.getRewards()){
            if(reward == null) continue;

            ItemStack item = reward.getItem();
            if(item == null || item.getType().isAir()) continue;

            double chance = reward.getChance();
            if(!Double.isFinite(chance) && chance <= 0) continue;

            rewards.add(reward);
        }

        return rewards;
    }
}
