package pl.karatiodev.cases.config.section;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;

@Getter
public class RewardSection extends OkaeriConfig {

    private ChanceLoreSection chanceLore = new ChanceLoreSection();

    @Getter
    public static class ChanceLoreSection extends OkaeriConfig {
        private boolean enabled = true;
        private String format = "<gray>Chance: <yellow>%chance%";
    }
}
