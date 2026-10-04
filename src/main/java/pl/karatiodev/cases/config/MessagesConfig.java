package pl.karatiodev.cases.config;

import eu.okaeri.configs.OkaeriConfig;
import lombok.Getter;

@Getter
public class MessagesConfig extends OkaeriConfig {

    private String caseExist = "<red>Case with ID <yellow>%id%</yellow> exists";
    private String errorCreate = "<red>Failed to create case.";
    private String caseCreated = "<green>Case created with ID <yellow>%id%";
    private String caseNotFound = "<red>Case with ID <yellow>%id%</yellow> not found.";
    private String caseDeleted = "<green>Case with ID <yellow>%id%</yellow> deleted.";
    private String positionCantChanged = "<red>Failed to change the case position.";
    private String positionChanged = "<green>Case position with ID <yellow>%id%</yellow> changed.";
    private String noChests = "<red>No configured chests.";
}
