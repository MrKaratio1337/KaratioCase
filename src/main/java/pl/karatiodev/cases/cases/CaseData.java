package pl.karatiodev.cases.cases;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CaseData extends OkaeriConfig {

    private String id = "";
    private String displayName = "<gold>Case";
    private Material blockType = Material.ENDER_CHEST;
    private CaseLocation location = new CaseLocation();
    private CaseKey key = new CaseKey();
    private List<CaseReward> rewards = new ArrayList<>();
}
