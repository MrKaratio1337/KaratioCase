package pl.karatiodev.cases.config.section;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class KeySection {

    private Material material = Material.TRIPWIRE_HOOK;
    private boolean glow = true;
    private int defaultCustomModelData = 1;
}
