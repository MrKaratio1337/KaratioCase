package pl.karatiodev.cases.config.section;

import lombok.Getter;

@Getter
public class AnimationSection {

    private boolean enabled = true;
    private int steps = 20;
    private long startDelay = 1L;
    private long maxDelay = 12L;
    private boolean closeProtection = true;
}
