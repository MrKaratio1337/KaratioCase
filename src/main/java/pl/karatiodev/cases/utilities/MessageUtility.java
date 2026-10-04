package pl.karatiodev.cases.utilities;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Map;

@UtilityClass
public class MessageUtility {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static Component deserialize(String text){
        if(text == null) return Component.empty();

        return MINI_MESSAGE.deserialize(text);
    }

    public static Component deserialize(String text, Map<String, String> placeholders){
        if(text == null) return Component.empty();

        String result = text;

        if(placeholders != null){
            for(Map.Entry<String, String> entry : placeholders.entrySet()){
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }

        return MINI_MESSAGE.deserialize(result);
    }
}
