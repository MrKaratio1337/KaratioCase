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
    private String outOfBound = "<red>Amount must be in 1 to 2304";
    private String cantCreateKey = "<red>Failed to create the key.";
    private String keyGived = "<green>Transferred <yellow>%amount%</yellow> for keys <yellow>%id%</yellow> to <yellow>%player%</yellow>";
    private String configReload = "<green>Configuration reloaded.";
    private String animationRunning = "<red>Animation is running";
    private String caseEmpty = "<red>That case is empty";
    private String noKey = "<red>You do not have key";
}
