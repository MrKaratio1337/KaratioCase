package pl.karatiodev.cases.config.section;

import lombok.Getter;

@Getter
public class RewardSection {

    @Getter
    public static class ChanceLoreSection {
        private boolean enabled = true;
        private String format = "<gray>Chance: <yellow>%chance%";
    }
}
