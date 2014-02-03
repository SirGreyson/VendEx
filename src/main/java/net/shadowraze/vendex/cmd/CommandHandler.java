package net.shadowraze.vendex.cmd;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.util.Messaging;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler implements CommandExecutor {

    private static CommandHandler instance = new CommandHandler();
    private static Map<String, RootCommand> commandMap = new HashMap<String, RootCommand>();

    public static CommandHandler getInstance() {
        return instance;
    }

    public void registerCommand(String name, RootCommand rootCommand) {
        VendEx.getPlugin().getCommand(name).setExecutor(instance);
        commandMap.put(name, rootCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(commandMap.containsKey(cmd.getName())) {
            if(!(sender instanceof Player)) Messaging.sendErrorMessage(sender, "Error! You cannot run this command from the console!");
            else commandMap.get(cmd.getName()).call((Player) sender, args);
            return true;
        } else return false;
    }
}
