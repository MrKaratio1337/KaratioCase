package pl.karatiodev.cases.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CaseItemUtility {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final String KEY_TYPE = "case-key";
    private static final String KEY_CASE_ID = "case-id";

    public static ItemStack createKey(CaseData caseData){
        CasePlugin plugin = CasePlugin.getInstance();

        Material material = plugin.getConfigs().getPluginConfig().getKey().getMaterial();

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;

        meta.displayName(MINI_MESSAGE.deserialize(caseData.getKey().getName()));

        int customModelData = caseData.getKey().getCustomModelData();
        if(customModelData > 0) meta.setCustomModelData(customModelData);

        if(plugin.getConfigs().getPluginConfig().getKey().isGlow()){
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        NamespacedKey typeKey = new NamespacedKey(plugin, KEY_TYPE);
        NamespacedKey caseIdKey = new NamespacedKey(plugin, KEY_CASE_ID);

        meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "true");
        meta.getPersistentDataContainer().set(caseIdKey, PersistentDataType.STRING, caseData.getId());

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isCaseKey(ItemStack item){
        if(item == null || item.getType().isAir()) return false;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;

        NamespacedKey key = new NamespacedKey(CasePlugin.getInstance(), KEY_TYPE);

        return meta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    public static String getCaseId(ItemStack item){
        if(item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if(meta == null) return null;

        NamespacedKey key = new NamespacedKey(CasePlugin.getInstance(), KEY_CASE_ID);

        return meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public static boolean isKeyForCase(ItemStack item, CaseData caseData){
        if(!isCaseKey(item)) return false;

        String caseId = getCaseId(item);
        return caseId != null && caseId.equalsIgnoreCase(caseData.getId());
    }

    public static void consumeOne(ItemStack item){
        if (item == null) return;

        int amount = item.getAmount();
        if(amount <= 1){
            item.setAmount(0);
            return;
        }

        item.setAmount(amount - 1);
    }

    public static List<Component> createRewardLore(List<Component> lore, double chance){
        List<Component> result = lore == null ? new ArrayList<>() : new ArrayList<>(lore);

        var chanceConfig = CasePlugin.getInstance().getConfigs().getPluginConfig().getRewards().getChanceLore();
        if(!chanceConfig.isEnabled()) return result;

        String format = chanceConfig.getFormat();
        if(format == null || format.isBlank()){
            format = "<gray>Chance: <yellow>%chance%%";
        }

        String chanceText = formatChance(chance);
        format = format.replace("%chance%", chanceText);

        result.add(MessageUtility.deserialize(format));
        return result;
    }

    public static ItemStack createDisplayReward(ItemStack original, double chance){
        if(original == null || original.getType().isAir()) return null;

        ItemStack display = original.clone();
        ItemMeta meta = display.getItemMeta();
        if(meta == null) return display;

        List<Component> lore = meta.lore();
        List<Component> newLore = createRewardLore(lore, chance);

        meta.lore(newLore);
        display.setItemMeta(meta);

        return display;
    }

    public static String formatChance(double chance){
        if (!Double.isFinite(chance)) return "0";

        if (chance < 0.0D) chance = 0.0D;
        if (chance == Math.rint(chance)) return String.format(Locale.US, "%.0f", chance);


        return String.format(Locale.US,"%.2f", chance).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
