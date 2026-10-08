package pl.karatiodev.cases.config.section;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import org.bukkit.Sound;

@Getter
public class SoundSection extends OkaeriConfig {

    private boolean enabled = true;

    private String rollingSound = "ui.button.click";
    private float rollingVolume = 1.0F;
    private float rollingPitch = 1.0F;

    private String rewardSound = "item.totem.use";
    private float rewardVolume = 1.0F;
    private float rewardPitch = 0.5F;
}
