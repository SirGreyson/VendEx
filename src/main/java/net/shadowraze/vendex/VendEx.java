package net.shadowraze.vendex;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.shadowraze.vendex.cmd.CommandHandler;
import net.shadowraze.vendex.cmd.cmds.MarketCmd;
import net.shadowraze.vendex.cmd.cmds.MenuCmd;
import net.shadowraze.vendex.cmd.cmds.ShopCmd;
import net.shadowraze.vendex.cmd.cmds.TradeCmd;
import net.shadowraze.vendex.market.MarketManager;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.trade.TradeHandler;
import net.shadowraze.vendex.util.Util;
import net.shadowraze.vendex.util.Variables;
import org.amhokies.votingRewards.VotingRewards;
import org.amhokies.votingRewards.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class VendEx extends JavaPlugin implements Listener {

    public static Economy economy;
    public static Permission permission;

    private static FileConfiguration persistenceConfig;

    public void onEnable() {
        if(!canEnable()) getServer().getPluginManager().disablePlugin(this);
        saveDefaultConfig();
        new Variables(this).loadVariables();
        MarketManager.instance.loadShops(this);
        MenuHandler.getInstance().loadMenus(this);
        MarketManager.getVendors();
        getServer().getPluginManager().registerEvents(MenuHandler.getInstance(), this);
        getServer().getPluginManager().registerEvents(TradeHandler.getInstance(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        CommandHandler.getInstance().registerCommand("menu", new MenuCmd());
        CommandHandler.getInstance().registerCommand("shop", new ShopCmd());
        CommandHandler.getInstance().registerCommand("market", new MarketCmd());
        CommandHandler.getInstance().registerCommand("trade", new TradeCmd());
        getLogger().info("has been enabled");
    }

    public void onDisable() {
        MarketManager.instance.saveShops(this);
        MarketManager.removeVendors();
        MenuHandler.closeAllInventories();
        getLogger().info("has been disabled");
    }

    private boolean canEnable() {
        if(getWorldGuard() == null) return false;
        else if(!getServer().getPluginManager().isPluginEnabled("Vault")) return false;
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if(permissionProvider != null && economyProvider != null) {
            permission = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
            economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }
        return economy != null && permission != null;
    }

    private WorldGuardPlugin getWorldGuard() {
        Plugin worldGuard = getServer().getPluginManager().getPlugin("WorldGuard");
        if(worldGuard == null | !(worldGuard instanceof WorldGuardPlugin)) return null;
        return (WorldGuardPlugin) worldGuard;
    }

    public static PlayerManager getVotingRewards() {
        if(Bukkit.getPluginManager().getPlugin("VotingRewards") == null) return null;
        return VotingRewards.getPlayerManager();
    }

    public static VendEx getPlugin() {
        return (VendEx) Bukkit.getPluginManager().getPlugin("VendEx");
    }

    public static FileConfiguration getPersistenceConfig() {
        if(persistenceConfig != null) return persistenceConfig;
        File configFile = new File(getPlugin().getDataFolder(), "persistenceConfig.yml");
        Util.validateFile(configFile);
        persistenceConfig = YamlConfiguration.loadConfiguration(configFile);
        return persistenceConfig;
    }

    public static void savePersistenceConfig() {
        Util.saveFile(new File(getPlugin().getDataFolder(), "persistenceConfig.yml"), getPersistenceConfig());
    }
}
