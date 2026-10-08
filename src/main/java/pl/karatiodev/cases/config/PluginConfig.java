package pl.karatiodev.cases.config;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import pl.karatiodev.cases.config.section.*;

@Getter
public class PluginConfig extends OkaeriConfig {

    private AnimationSection animation = new AnimationSection();
    private GuiSection gui = new GuiSection();
    private SecuritySection security = new SecuritySection();
    private KeySection key = new KeySection();
    private RewardSection rewards = new RewardSection();
    private SoundSection sounds = new SoundSection();
}
