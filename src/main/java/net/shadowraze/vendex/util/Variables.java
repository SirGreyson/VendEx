package net.shadowraze.vendex.util;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.shadowraze.vendex.VendEx;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Variables {

    private VendEx plugin;
    public Variables(VendEx plugin) {
        this.plugin = plugin;
    }

    public static String PLUGIN_PREFIX;
    public static String ADMIN_PERM;

    public static Map<String, String> MESSAGES = new HashMap<String, String>();
    public static Map<String, String> ERRMESSAGES = new HashMap<String, String>();

    public static boolean WG_ENABLED;
    public static ProtectedRegion WG_REGION;
    public static String REGION_ERROR_MSG;

    public void loadVariables() {
        PLUGIN_PREFIX = Util.parseColor(plugin.getConfig().getString("pluginPrefix"));
        ADMIN_PERM = plugin.getConfig().getString("adminPermission");

        for(String message : plugin.getConfig().getConfigurationSection("messages").getKeys(false))
            MESSAGES.put(message, Util.parseColor(plugin.getConfig().getString("messages." + message)));
        for(String errMessage : plugin.getConfig().getConfigurationSection("errMessages").getKeys(false))
            ERRMESSAGES.put(errMessage, Util.parseColor(plugin.getConfig().getString("errMessages." + errMessage)));

        WG_ENABLED = plugin.getConfig().getBoolean("worldGuard.enabled");
        if(WG_ENABLED) WG_REGION = WGBukkit.getRegionManager(Bukkit.getWorld(plugin.getConfig().getString("worldGuard.world"))).getRegionExact(plugin.getConfig().getString("worldGuard.regionName"));
        REGION_ERROR_MSG = plugin.getConfig().getString("worldGuard.errorMsg");
    }
}
