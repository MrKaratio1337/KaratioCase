package pl.karatiodev.cases.inventories;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CaseInventoryItems {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    public static ItemStack createButton(Material material, String name){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;

        meta.displayName(MINI_MESSAGE.deserialize(name));

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createFiller(){
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            meta.displayName(MINI_MESSAGE.deserialize("<gray>"));

            item.setItemMeta(meta);
        }

        return item;
    }
}
