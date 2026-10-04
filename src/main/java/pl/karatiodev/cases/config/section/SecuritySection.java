package pl.karatiodev.cases.config.section;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;

@Getter
public class SecuritySection extends OkaeriConfig {

    private boolean removeEditorTags = true;
    private boolean refreshRewardUuid = true;
    private boolean preventCloseAnimation = true;
}
