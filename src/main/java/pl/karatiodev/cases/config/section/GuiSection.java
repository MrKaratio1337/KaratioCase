package pl.karatiodev.cases.config.section;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;

@Getter
public class GuiSection extends OkaeriConfig {

    private String previewTitle = "<gold>Case: <yellow>%case%</yellow>";
    private String animationTitle = "<gold>Opening chest";

    private int previewSize = 54;
    private int animationSize = 27;

    private int animatedButtonSlot = 45;
    private int instantButtonSlot = 49;
    private int closeButtonSlot = 49;

    private String animatedButtonMaterial = "LIME_DYE";
    private String instantButtonMaterial = "GOLD_INGOT";
    private String closeButtonMaterial = "BARRIER";

    private String animatedButtonName = "<green>Open with animation";
    private String instantButtonName = "<gold>Open instantly";
    private String closeButtonName = "<red>Close";
}
