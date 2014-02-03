package net.shadowraze.vendex.cmd;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class RootCommand {

    Map<String, SubCommand> subCommands = new HashMap<String, SubCommand>();

    public RootCommand() {
        registerSubCommands();
    }

    public boolean call(Player player, String[] args) {
        return onCommand(player, args);
    }

    public void registerSubCommand(String name, SubCommand command) {
        subCommands.put(name, command);
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    public abstract void registerSubCommands();

    public abstract boolean onCommand(Player player, String args[]);

    public abstract String helpMessage();
}
