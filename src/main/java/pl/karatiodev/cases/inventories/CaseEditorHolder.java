package pl.karatiodev.cases.inventories;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pl.karatiodev.cases.cases.CaseData;

@Getter
@RequiredArgsConstructor
public class CaseEditorHolder implements InventoryHolder {

    private final CaseData caseData;

    @Setter
    private Inventory inventory;

    @Override
    public Inventory getInventory(){
        return inventory;
    }
}
