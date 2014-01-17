package net.shadowraze.vendex;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.shadowraze.vendex.market.MarketUtil;
import net.shadowraze.vendex.market.OfferType;
import net.shadowraze.vendex.market.Shop;
import net.shadowraze.vendex.market.ShopItem;
import net.shadowraze.vendex.util.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VendEx extends JavaPlugin {

    public static Economy economy;
    public static Permission permission;
    public static List<Shop> playerShops = new ArrayList<Shop>();

    public void onEnable() {
        if(!canEnable()) getServer().getPluginManager().disablePlugin(this);
        loadPlayerShops();
        getLogger().info("has been enabled");
    }

    public void onDisable() {
        savePlayerShops();
        getLogger().info("has been disabled");
    }

    public boolean canEnable() {
        if(!getServer().getPluginManager().isPluginEnabled("Vault")) return false;
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if(economyProvider == null) return false;
        economy = economyProvider.getProvider();
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        permission = permissionProvider.getProvider();
        return economy != null && permission != null;
    }

    public void loadPlayerShops() {
        File shopFolder = new File(getDataFolder() + File.pathSeparator + "shops");
        Util.validateFile(shopFolder);
        for (File shopFile : shopFolder.listFiles()) {
            if (shopFile.getName().contains(".yml")) {
                FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
                String shopOwner = shopFile.getName().split(".yml")[0];
                int shopSize = MarketUtil.getShopSize(shopOwner);
                Shop playerShop = new Shop(shopOwner, shopSize);
                if (shopConfig.getConfigurationSection("shopItems") != null)
                    for (int i = 0; i < shopSize; i++)
                        if (shopConfig.getConfigurationSection("shopItems." + i) != null)
                            new ShopItem(playerShop, shopConfig.getItemStack("shopItems." + i + ".itemStack"),
                                    OfferType.valueOf(shopConfig.getString("shopItems." + i + ".offerType")),
                                    shopConfig.getInt("shopItems." + i + ".price"), shopConfig.getInt("shopItems." + i + ".amount"));
            }
        }
    }

    public void savePlayerShops() {
        for(Shop playerShop : playerShops)
            playerShop.saveToFile(this);
    }
}