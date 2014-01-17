package net.shadowraze.vendex.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messaging {

    private static String messagePrefix = Variables.PLUGIN_PREFIX;

    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(messagePrefix + message.replaceAll("&", "§"));
    }

    public static void sendErrorMessage(CommandSender sender, String message){
        sender.sendMessage(messagePrefix + ChatColor.RED + message.replaceAll("&", "§"));
    }

    public static void sendServerMessage(String message) {
        Bukkit.getServer().broadcastMessage(messagePrefix + message.replace("&", "§"));
    }
}
