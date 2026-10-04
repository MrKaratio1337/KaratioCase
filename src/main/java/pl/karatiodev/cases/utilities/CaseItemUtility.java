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

    public static List<Component> createRewardLore(List<Component> originalLore, double chance){
        List<Component> lore = originalLore == null ? new ArrayList<>() : new ArrayList<>(originalLore);

        CasePlugin plugin = CasePlugin.getInstance();

        var section = plugin.getConfigs().getPluginConfig().getRewards().getChanceLore();
        if(!section.isEnabled()) return lore;

        String chanceText = formatChance(chance);
        String formatted = section.getFormat().replace("%chance%", chanceText);

        lore.add(MINI_MESSAGE.deserialize(formatted));
        return lore;
    }

    public static ItemStack createDisplayReward(ItemStack original, double chance){
        if(original == null) return null;

        ItemStack clone = original.clone();
        ItemMeta meta = clone.getItemMeta();
        if(meta == null) return clone;

        meta.lore(createRewardLore(meta.lore(), chance));

        clone.setItemMeta(meta);
        return clone;
    }

    public static String formatChance(double chance){
        if(chance == Math.rint(chance)) return String.format("%.0f", chance);

        return String.format("%.2f", chance).replaceAll("0+$", "");
    }
}
