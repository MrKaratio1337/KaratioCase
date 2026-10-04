package pl.karatiodev.cases.config;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;
import pl.karatiodev.cases.cases.CaseData;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class CasesConfig extends OkaeriConfig {

    private Map<String, CaseData> cases = new LinkedHashMap<>();
}
