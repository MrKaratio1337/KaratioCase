package pl.karatiodev.cases.config.section;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;

@Getter
public class GuiSection extends OkaeriConfig {

    private String previewTitle = "<gold>Case: <yellow>%case%</yellow>";
    private String animationTitle = "<gold>Opening chest";
    private int previewSize = 54;
    private int animationSize = 27;
}
