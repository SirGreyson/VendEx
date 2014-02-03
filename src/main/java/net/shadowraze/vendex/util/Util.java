package net.shadowraze.vendex.util;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static ItemStack metaStack(String name, List<String> lore, Material material) {
        ItemStack tempStack = new ItemStack(material);
        ItemMeta tempMeta = tempStack.getItemMeta();
        if(name != null) tempMeta.setDisplayName(parseColor(name));
        List<String> parsedLore = new ArrayList<String>();
        for(String loreItem : lore)
        parsedLore.add(parseColor(loreItem));
        tempMeta.setLore(parsedLore);
        tempStack.setItemMeta(tempMeta);
        return tempStack;
    }

    public static ItemStack metaStack(String name, List<String> lore, Material material, Byte data) {
        ItemStack tempStack = new ItemStack(material, 1, data);
        ItemMeta tempMeta = tempStack.getItemMeta();
        if(name != null) tempMeta.setDisplayName(parseColor(name));
        List<String> parsedLore = new ArrayList<String>();
        for(String loreItem : lore)
            parsedLore.add(parseColor(loreItem));
        tempMeta.setLore(parsedLore);
        tempStack.setItemMeta(tempMeta);
        return tempStack;
    }

    public static String parseColor(String coloredString) {
        return coloredString.replace("&", "§");
    }

    public static String stripColor(String coloredString) {
        return coloredString.substring(coloredString.indexOf("§") + 2, coloredString.length());
    }

    public static String parseLocation(Location location) {
        return location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getWorld().getName();
    }

    public static Location parseLocString(String locString) {
        String[] locParts = locString.split(",");
        return new Location(Bukkit.getWorld(locParts[3]), Integer.parseInt(locParts[0]), Integer.parseInt(locParts[1]), Integer.parseInt(locParts[2]));
    }

    public static boolean isInRegion(Entity entity, ProtectedRegion protectedRegion) {
        Location loc = entity.getLocation();
        if(protectedRegion.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) return true;
        return false;
    }

    public static void validateFile(File checkFile) {
        if(checkFile.exists()) return;
        try {
            checkFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile(File saveFile, FileConfiguration saveConfig) {
        try {
            saveConfig.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
