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
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.Area;
import pl.karatiodev.cases.CasePlugin;
import pl.karatiodev.cases.cases.CaseData;
import pl.karatiodev.cases.utilities.MessageUtility;

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
    }
}
