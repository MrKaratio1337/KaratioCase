package pl.karatiodev.cases.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Sender;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.Area;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.inventories.CaseEditorInventory;
import pl.karatiodev.cases.utilities.MessageUtility;

import java.util.Map;

@Command(name = "case", aliases = {"cases", "karatiocase"})
@Permission("karatiocase.admin")
@RequiredArgsConstructor
public class CaseCommand {

    private final CasePlugin plugin;

    @Execute
    public void help(@Sender CommandSender sender){
        sender.sendMessage(MessageUtility.deserialize("<gold>KaratioCase <gray>- admin commands"));
        sender.sendMessage(MessageUtility.deserialize("<yellow>/case create <id> <name>"));
        sender.sendMessage(MessageUtility.deserialize("<yellow>/case delete <id>"));
        sender.sendMessage(MessageUtility.deserialize("<yellow>/case setpos <id>"));
        sender.sendMessage(MessageUtility.deserialize("<yellow>/case list"));
        sender.sendMessage(MessageUtility.deserialize("<yellow>/case info <id>"));
        sender.sendMessage(MessageUtility.deserialize("<yellow>/case edit <id>"));
        sender.sendMessage(MessageUtility.deserialize("<yellow>/case reload"));
    }

    @Execute(name = "create")
    public void create(@Sender Player player, @Arg String id, @Arg String name){
        if(plugin.getCaseManager().exists(id)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseExist().replace("%id%", id)));
            return;
        }

        CaseData caseData = plugin.getCaseManager().create(id, name,
                player.getTargetBlockExact(10) != null ? player.getTargetBlockExact(10).getType() : Material.ENDER_CHEST, player.getLocation());

        if(caseData == null){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getErrorCreate()));
            return;
        }

        player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseCreated().replace("%id%", caseData.getId())));
    }

    @Execute(name = "delete")
    public void delete(@Sender CommandSender sender, @Arg String id){
        if(!plugin.getCaseManager().delete(id)){
            sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseNotFound().replace("%id%", id)));
            return;
        }

        plugin.getCaseManager().delete(id);
        sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseDeleted().replace("%id%", id)));
    }

    @Execute(name = "setpos")
    public void setPosition(@Sender Player player, @Arg String id){
        CaseData caseData = plugin.getCaseManager().get(id);

        if(caseData == null){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseNotFound().replace("%id%", id)));
            return;
        }

        if(!plugin.getCaseManager().move(id, player.getLocation())){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getPositionCantChanged()));
            return;
        }

        plugin.getCaseManager().move(id, player.getLocation());
        player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getPositionChanged().replace("%id%", caseData.getId())));
    }

    @Execute(name = "list")
    public void list(@Sender CommandSender sender){
        if(plugin.getCaseManager().getCases().isEmpty()){
            sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getNoChests()));
            return;
        }

        sender.sendMessage(MessageUtility.deserialize("<gold>Configured cases:"));
        plugin.getCaseManager().getCases().forEach(caseData -> {
            sender.sendMessage(MessageUtility.deserialize("<gray>- <yellow>" + caseData.getId() + " <dark_gray>(" + caseData.getRewards().size() + " rewards)"));
        });
    }

    @Execute(name = "info")
    public void info(@Sender CommandSender sender, @Arg String id){
        CaseData caseData = plugin.getCaseManager().get(id);

        if(caseData == null){
            sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseNotFound().replace("%id%", id)));
            return;
        }

        sender.sendMessage(MessageUtility.deserialize("<gold>Case information:"));
        sender.sendMessage(MessageUtility.deserialize("<gray>ID <yellow>" + caseData.getId()));
        sender.sendMessage(MessageUtility.deserialize("<gray>Name <yellow>" + caseData.getDisplayName()));
        sender.sendMessage(MessageUtility.deserialize("<gray>Block <yellow>" + caseData.getBlockType()));
        sender.sendMessage(MessageUtility.deserialize("<gray>World <yellow>" + caseData.getLocation().getWorld()));
        sender.sendMessage(MessageUtility.deserialize("<gray>Rewards size <yellow>" + caseData.getRewards().size()));
    }

    @Execute(name = "givekey")
    public void giveKey(@Sender CommandSender sender, @Arg String id){
        if(!(sender instanceof Player player)){
            sender.sendMessage(MessageUtility.deserialize("<red>That subcommand must be used by player"));
            return;
        }

        giveKey(player, player, id, 1);
    }

    @Execute(name = "givekey")
    public void giveKey(@Sender Player sender, @Arg String id, @Arg int amount){
        giveKey(sender, sender, id, amount);
    }

    @Execute(name = "edit")
    public void edit(@Sender Player player, @Arg String id){
        CaseData caseData = plugin.getCaseManager().get(id);
        if(caseData == null){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseNotFound()));
            return;
        }

        if(plugin.getAnimationManager().isRunning(player)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getAnimationRunning()));
            return;
        }

        if(plugin.getCaseEditorManager().isEditing(player)){
            player.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getEditing()));
            return;
        }

        CaseEditorInventory.open(player, caseData);
    }

    @Execute(name = "reload")
    public void reload(@Sender CommandSender sender){
        plugin.getConfigs().reload(plugin);
        plugin.getCaseManager().load();

        sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getConfigReload()));
    }

    private void giveKey(CommandSender sender, Player target, String id, int amount){
        CaseData caseData = plugin.getCaseManager().get(id);
        if(caseData == null){
            sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCaseNotFound()));
            return;
        }

        if(amount < 1 || amount > 2304){
            sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getOutOfBound()));
            return;
        }

        ItemStack key = plugin.getCaseManager().createKey(id);
        if(key == null){
            sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getCantCreateKey()));
            return;
        }

        giveItems(target, key, amount);
        sender.sendMessage(MessageUtility.deserialize(plugin.getConfigs().getMessagesConfig().getKeyGived().replace("%amount%", String.valueOf(amount)).replace("%id%", id).replace("%player%", target.getName())));
    }

    private void giveItems(Player player, ItemStack item, int amount){
        int maxStack = item.getMaxStackSize();
        int remaining = amount;

        while(remaining > 0){
            int currentAmount = Math.min(remaining, maxStack);

            ItemStack clone = item.clone();
            clone.setAmount(currentAmount);

            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(clone);
            leftovers.values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));

            remaining -= currentAmount;
        }
    }
}
