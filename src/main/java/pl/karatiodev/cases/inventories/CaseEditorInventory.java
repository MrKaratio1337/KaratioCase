package pl.karatiodev.cases.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseReward;
import pl.karatiodev.cases.utilities.MessageUtility;

import java.util.function.Consumer;

public class CaseEditorInventory {

    private static final int[] REWARD_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    public static void open(Player player, CaseData caseData){
        CasePlugin plugin = CasePlugin.getInstance();

        var config = plugin.getConfigs().getPluginConfig().getGui();

        int size = normalizeSize(config.getPreviewSize());
        String title = config.getEditingTitle();

        CaseEditorHolder holder = new CaseEditorHolder(caseData);
        Inventory inventory = Bukkit.createInventory(holder, size, MessageUtility.deserialize(title));
        holder.setInventory(inventory);

        fillBackground(inventory);

        int index = 0;

        for (CaseReward reward : plugin.getCaseManager().getRewardManager().getValidRewards(caseData)) {
            if (index >= REWARD_SLOTS.length) break;
            if (reward == null || reward.getItem() == null || reward.getItem().getType().isAir()) continue;


            inventory.setItem(REWARD_SLOTS[index], reward.getItem().clone());
            index++;
        }

        inventory.setItem(45, createButton(Material.LIME_WOOL, "<green>Save"));
        inventory.setItem(49, createButton(Material.RED_WOOL, "<red>Cancel"));
        inventory.setItem(53, createButton(Material.BARRIER, "<red>Clear rewards"));

        player.openInventory(inventory);

        plugin.getCaseEditorManager().start(player, caseData);
    }

    private static void fillBackground(Inventory inventory){
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, createFiller());
        }

        for (int slot : REWARD_SLOTS) {
            inventory.setItem(slot, null);
        }
    }

    private static ItemStack createFiller(){
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;

        meta.displayName(MessageUtility.deserialize("<gray>"));

        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack createButton(Material material, String name){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;

        meta.displayName(MessageUtility.deserialize(name));

        item.setItemMeta(meta);

        return item;
    }

    private static int normalizeSize(int size) {
        if(size < 54) return 54;
        if(size > 54) return 54;

        return size;
    }
}
