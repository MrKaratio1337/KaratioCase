package pl.karatiodev.cases.config.section;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public class KeySection extends OkaeriConfig {

    private Material material = Material.TRIPWIRE_HOOK;
    private boolean glow = true;
    private int defaultCustomModelData = 1;
}
