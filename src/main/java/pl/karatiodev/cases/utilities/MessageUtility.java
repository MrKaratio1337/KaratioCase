package pl.karatiodev.cases.utilities;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

@UtilityClass
public class MessageUtility {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    public static Component deserialize(String text){
        return MINI.deserialize(text);
    }
}
