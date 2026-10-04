package pl.karatiodev.cases.cases;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseKey extends OkaeriConfig {

    private String name = "<white>Key";
    private int customModelData = 1;
}
