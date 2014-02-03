package net.shadowraze.vendex.cmd;

import org.bukkit.entity.Player;

public interface SubCommand {

    public boolean onCommand(Player player, String args[]);

    public String helpMessage();
}
