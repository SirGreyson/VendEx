package net.shadowraze.vendex.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messaging {

    private static String messagePrefix = Variables.PLUGIN_PREFIX + " ";

    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(messagePrefix + message.replaceAll("&", "§"));
    }

    public static void sendErrorMessage(CommandSender sender, String message){
        sender.sendMessage(messagePrefix + ChatColor.RED + message.replaceAll("&", "§"));
    }
}
