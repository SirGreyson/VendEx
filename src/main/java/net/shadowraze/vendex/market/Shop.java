package net.shadowraze.vendex.market;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Shop {

    private String shopOwner;
    private List<ShopOffer> shopOffers;
    private Inventory shopInventory;
    private boolean isServerShop;

    public Shop(String shopOwner, boolean isServerShop) {
        this.shopOwner = shopOwner;
        this.shopOffers = new ArrayList<ShopOffer>();
        if(isServerShop) this.shopInventory = Bukkit.createInventory(null, VendEx.getPlugin().getConfig().getInt("shopConfig.serverShop.size"), Util.parseColor(VendEx.getPlugin().getConfig().getString("shopConfig.serverShop.title")));
        else this.shopInventory = Bukkit.createInventory(null, MarketManager.getPlayerShopSize(shopOwner), Util.parseColor(VendEx.getPlugin().getConfig().getString("shopConfig.playerShop.prefix") + shopOwner + "'s Shop"));
        this.isServerShop = isServerShop;
    }

    public String getShopOwner() {
        return shopOwner;
    }

    public List<ShopOffer> getShopOffers() {
        return shopOffers;
    }

    public void addShopOffer(ShopOffer shopOffer) {
        shopOffers.add(shopOffer);
        shopInventory.addItem(shopOffer.getMenuStack());
    }

    public void removeShopOffer(ShopOffer shopOffer) {
        shopInventory.remove(shopInventory.getItem(shopOffers.indexOf(shopOffer)));
        shopOffers.remove(shopOffer);
    }

    public void updateShopOffer(ShopOffer shopOffer) {
        shopInventory.setItem(shopOffers.indexOf(shopOffer), shopOffer.getMenuStack());
    }

    public Inventory getShopInventory() {
        return shopInventory;
    }

    public void loadShopInventory() {
        shopInventory.clear();
        for(int i = 0; i < shopOffers.size(); i++)
            shopInventory.setItem(i, shopOffers.get(i).getMenuStack());
    }

    public boolean isServerShop() {
        return isServerShop;
    }

    public void saveShop(VendEx plugin) {
        File shopFile = isServerShop ? new File(plugin.getDataFolder(), "serverShop.yml") :
                new File(plugin.getDataFolder() + "/shops", shopOwner + ".yml");
        Util.validateFile(shopFile);
        FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        shopConfig.set("shopOffers", null);
        for(int i = 0; i < shopOffers.size(); i++) {
            shopConfig.set("shopOffers." + i + ".shopPrice", shopOffers.get(i).getShopPrice());
            if(isServerShop() && shopOffers.get(i).hasBoundCmd()) shopConfig.set("shopOffers." + i + ".boundCmd", shopOffers.get(i).getBoundCmd());
            shopConfig.set("shopOffers." + i + ".shopAmount", shopOffers.get(i).getShopAmount());
            shopConfig.set("shopOffers." + i + ".itemStack", shopOffers.get(i).getItemStack());
        }
        Util.saveFile(shopFile, shopConfig);
    }

    public void deleteShop(VendEx plugin) {
        File shopFile = new File(plugin.getDataFolder() + "/shops", shopOwner + ".yml");
        shopFile.delete();
    }
}
