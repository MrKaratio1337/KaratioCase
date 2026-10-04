package pl.karatiodev.cases.cases;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CaseData {

    private String id, displayName;
    private Material blockType = Material.ENDER_CHEST;
    private CaseLocation location = new CaseLocation();
    private CaseKey key = new CaseKey();
    private List<CaseReward> rewards = new ArrayList<>();
}
