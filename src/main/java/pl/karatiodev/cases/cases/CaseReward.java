package pl.karatiodev.cases.cases;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaseReward extends OkaeriConfig {

    private ItemStack item;
    private double chance = 1.0D;

    public ItemStack getItemClone(){
        return item == null ? null : item.clone();
    }
}
