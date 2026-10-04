package pl.karatiodev.cases.inventories;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.cases.CaseReward;
import pl.karatiodev.cases.utilities.CaseItemUtility;
import pl.karatiodev.cases.utilities.MessageUtility;

public final class PreviewInventory {

    public static void open(Player player, CaseData caseData){
        CasePlugin plugin = CasePlugin.getInstance();

        var guiConfig = plugin.getConfigs().getPluginConfig().getGui();

        int size = normalizeSize(guiConfig.getPreviewSize());
        String title = guiConfig.getPreviewTitle().replace("%case%", caseData.getDisplayName());

        PreviewHolder holder = new PreviewHolder(caseData);
        Inventory inventory = Bukkit.createInventory(holder, size, MessageUtility.deserialize(title));
        holder.setInventory(inventory);

        for(int slot = 0; slot < size; slot++){
            inventory.setItem(slot, CaseInventoryItems.createFiller());
        }

        int rewardSlot = 10;

        for(CaseReward reward : caseData.getRewards()){
            if(reward == null) continue;
            if(reward.getItem() == null) continue;
            if(reward.getChance() <= 0) continue;
            if(rewardSlot >= 44) break;

            inventory.setItem(rewardSlot, CaseItemUtility.createDisplayReward(reward.getItem(), reward.getChance()));


            rewardSlot++;

            if(rewardSlot == 17) rewardSlot = 19;
            if(rewardSlot == 26) rewardSlot = 28;
            if(rewardSlot == 35) rewardSlot = 37;
        }

        Material animatedMaterial = materialOrDefault(guiConfig.getAnimatedButtonMaterial(), Material.LIME_DYE);
        Material instantMaterial = materialOrDefault(guiConfig.getInstantButtonMaterial(), Material.GOLD_INGOT);
        Material closeMaterial = materialOrDefault(guiConfig.getCloseButtonMaterial(), Material.BARRIER);

        inventory.setItem(guiConfig.getAnimatedButtonSlot(), CaseInventoryItems.createButton(animatedMaterial, guiConfig.getAnimatedButtonName()));
        inventory.setItem(guiConfig.getInstantButtonSlot(), CaseInventoryItems.createButton(instantMaterial, guiConfig.getInstantButtonName()));
        inventory.setItem(guiConfig.getCloseButtonSlot(), CaseInventoryItems.createButton(closeMaterial, guiConfig.getCloseButtonName()));

        player.openInventory(inventory);
    }

    private static int normalizeSize(int size){
        if(size < 9) return 9;
        if(size > 54) return 54;

        return size - (size % 9);
    }

    private static Material materialOrDefault(String name, Material fallback){
        Material material = Material.matchMaterial(name);
        return material == null ? fallback : material;
    }
}
