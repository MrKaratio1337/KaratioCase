package pl.karatiodev.cases.config;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import pl.karatiodev.cases.config.section.AnimationSection;
import pl.karatiodev.cases.config.section.GuiSection;
import pl.karatiodev.cases.config.section.KeySection;
import pl.karatiodev.cases.config.section.SecuritySection;

@Getter
public class PluginConfig extends OkaeriConfig {

    private AnimationSection animation = new AnimationSection();
    private GuiSection gui = new GuiSection();
    private SecuritySection security = new SecuritySection();
    private KeySection key = new KeySection();
}
