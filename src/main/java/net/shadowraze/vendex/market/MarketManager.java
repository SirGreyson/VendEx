package net.shadowraze.vendex.market;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketManager {

    public static Shop serverShop = new Shop("serverShop", true);
    public static List<Shop> playerShops;
    public static MarketManager instance = new MarketManager();

    public static Map<Location, Villager> vendorMap;
    public static String vendorName = Util.parseColor(VendEx.getPlugin().getConfig().getString("shopConfig.vendorName"));

    private static List<Material> blackList;
    private boolean intervalEnabled;
    private int intervalTicks;

    public void loadShops(VendEx plugin) {
        playerShops = new ArrayList<Shop>();
        File shopFolder = new File(plugin.getDataFolder() + "/shops");
        Util.validateFile(shopFolder);
        plugin.getLogger().info("Loading shop inventories...");
        for (File shopFile : shopFolder.listFiles()) {
            if (shopFile.getName().contains(".yml")) {
                FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
                String shopOwner = shopFile.getName().split(".yml")[0];
                Shop playerShop = new Shop(shopOwner, false);
                for(int i = 0; i < getPlayerShopSize(shopOwner) * 9; i++) {
                    if(shopConfig.getConfigurationSection("shopOffers." + i) == null) break;
                    playerShop.addShopOffer(new ShopOffer(playerShop, shopConfig.getItemStack("shopOffers." + i + ".itemStack"),
                            shopConfig.getInt("shopOffers." + i + ".shopPrice"), shopConfig.getInt("shopOffers." + i + ".shopAmount")));
                }
                playerShops.add(playerShop);
            } else plugin.getLogger().severe("Unknown file: " + shopFile.getName() + " in shops folder");
        }  loadServerShop(plugin);
        plugin.getLogger().info("All shop inventories loaded!");
        runTimer(plugin);
    }

    private void loadServerShop(VendEx plugin) {
        File shopFile = new File(plugin.getDataFolder(), "serverShop.yml");
        Util.validateFile(shopFile);
        plugin.getLogger().info("Loading server shop inventory...");
        FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        for(int i = 0; i < plugin.getConfig().getInt("shopConfig.serverShop.size"); i++) {
            if(shopConfig.getConfigurationSection("shopOffers." + i) == null) break;
            if(shopConfig.getString("shopOffers." + i + ".boundCmd") != null)
                serverShop.addShopOffer(new ShopOffer(serverShop, shopConfig.getItemStack("shopOffers." + i + ".itemStack"),
                        shopConfig.getInt("shopOffers." + i + ".shopPrice"), shopConfig.getString("shopOffers." + i + ".boundCmd")));
            else serverShop.addShopOffer(new ShopOffer(serverShop, shopConfig.getItemStack("shopOffers." + i + ".itemStack"),
                    shopConfig.getInt("shopOffers." + i + ".shopPrice")));
        }
        plugin.getLogger().info("Server shop inventory loaded!");
    }

    public void saveShops(VendEx plugin) {
        plugin.getLogger().info("Saving shop inventories...");
        for(Shop shop : playerShops) shop.saveShop(plugin);
        plugin.getLogger().info("Saving server shop inventories...");
        serverShop.saveShop(plugin);
        plugin.getLogger().info("All shop inventories saved!");
    }

    public void runTimer(final VendEx plugin) {
        intervalEnabled = plugin.getConfig().getBoolean("shopConfig.saveInterval.enabled");
        intervalTicks = plugin.getConfig().getInt("shopConfig.saveInterval.ticks");
        if(intervalEnabled) plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
               saveShops(plugin);
            }
        }, intervalTicks, intervalTicks);
    }

    public static void addShop(String shopOwner) {
        Shop newShop = new Shop(shopOwner, false);
        playerShops.add(newShop);
        newShop.saveShop(VendEx.getPlugin());
        MenuHandler.MARKET_MENU.addShop(newShop);
    }

    public static void removeShop(Shop remShop) {
        playerShops.remove(remShop);
        remShop.deleteShop(VendEx.getPlugin());
    }

    public static boolean canRemoveShop(Shop remShop) {
        boolean shopEmpty = true;
        for(int i = 0; i < remShop.getShopOffers().size() && shopEmpty; i++)
            if(remShop.getShopOffers().get(i).getShopAmount() > 0) shopEmpty = false;
        return shopEmpty;
    }

    public static Shop getPlayerShop(String playerName) {
        if(playerName.equalsIgnoreCase("serverShop")) return serverShop;
        for(Shop playerShop : playerShops)
            if(playerShop.getShopOwner().equalsIgnoreCase(playerName)) return playerShop;
        return null;
    }

    public static int getPlayerShopSize(String playerName) {
        for(int i = 6; i > 0; i--)
            if(VendEx.permission.playerHas((World) null, playerName, "vendex.shopsize." + i)) return i * 9;
        return 9;
    }

    public static List<Shop> shopsWithSize(int shopSize) {
        List<Shop> shopList = new ArrayList<Shop>();
        for(Shop playerShop : playerShops)
            if(playerShop.getShopInventory().getSize() == shopSize) shopList.add(playerShop);
        return shopList;
    }

    public static List<Material> getBlackList() {
        if (blackList != null) return blackList;
        blackList = new ArrayList<Material>();
        for (String material : VendEx.getPlugin().getConfig().getStringList("shopConfig.blackList"))
            blackList.add(Material.valueOf(material));
        return blackList;
    }

    public static Map<Location, Villager> getVendors() {
        if(vendorMap != null) return vendorMap;
        vendorMap = new HashMap<Location, Villager>();
        for(String locString : VendEx.getPersistenceConfig().getStringList("vendorLocations"))
            vendorMap.put(Util.parseLocString(locString), vendorVillager(Util.parseLocString(locString)));
        return vendorMap;
    }

    public static void saveVendors() {
        List<String> locList = new ArrayList<String>();
        for(Location loc : getVendors().keySet())
            locList.add(Util.parseLocation(loc));
        VendEx.getPersistenceConfig().set("vendorLocations", locList);
        VendEx.savePersistenceConfig();
    }

    public static void removeVendors() {
        for(Villager vendor : getVendors().values())
            vendor.setHealth(0);
    }

    public static Villager vendorVillager(Location spawnLoc) {
        Villager vendor = (Villager) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.VILLAGER);
        vendor.setCustomName(vendorName);
        vendor.setProfession(Villager.Profession.BLACKSMITH);
        vendor.setRemoveWhenFarAway(false);
        return vendor;
    }
}
