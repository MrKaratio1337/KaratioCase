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
        if(caseData == null || caseData.getRewards() != null) return null;

        List<CaseReward> validRewards = new ArrayList<>();

        for(CaseReward reward : caseData.getRewards()){
            if(reward == null) continue;

            ItemStack item = reward.getItem();
            if(item == null || item.getType().isAir()) continue;

            if(reward.getChance() <= 0.0D) continue;

            validRewards.add(reward);
        }

        if(validRewards.isEmpty()) return null;

        double totalWeight = validRewards.stream().mapToDouble(CaseReward::getChance).sum();
        if(totalWeight <= 0.0D) return null;

        double random = ThreadLocalRandom.current().nextDouble(totalWeight);

        double current = 0.0D;

        for(CaseReward reward : validRewards){
            current += reward.getChance();

            if(random < current){
                return reward;
            }
        }

        return validRewards.get(validRewards.size() - 1);
    }

    public List<CaseReward> getValidRewards(CaseData caseData){
        if(caseData == null || caseData.getRewards() == null) return List.of();

        return caseData.getRewards()
                .stream()
                .filter(reward -> reward != null)
                .filter(reward -> reward.getItem() != null)
                .filter(reward -> !reward.getItem().getType().isAir())
                .filter(reward -> reward.getChance() > 0.0D)
                .toList();
    }
}
