package pl.karatiodev.cases.manager;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import pl.karatiodev.cases.cases.CaseReward;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CaseEditorSession {

    private final UUID playerId;
    private final String caseId;
    private final List<CaseReward> originalRewards;
    private final List<ItemStack> originalItems;

    @Setter
    private boolean saved;

    public CaseEditorSession(UUID playerId, String caseId, List<CaseReward> rewards){
        this.playerId = playerId;
        this.caseId = caseId;
        this.originalRewards = new ArrayList<>();
        this.originalItems = new ArrayList<>();

        if(rewards == null) return;

        for(CaseReward reward : rewards){
            if(reward == null) continue;

            CaseReward copy = new CaseReward();
            copy.setItem(reward.getItem() == null ? null : reward.getItem().clone());
            copy.setChance(reward.getChance());

            originalRewards.add(copy);

            if(reward.getItem() != null){
                originalItems.add(reward.getItem().clone());
            }
        }
    }
}
