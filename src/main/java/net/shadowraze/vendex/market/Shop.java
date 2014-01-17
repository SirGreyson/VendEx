package net.shadowraze.vendex.market;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.menus.ShopMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Shop {

    private String owner;
    private List<ShopItem> shopItems;
    private ShopMenu shopMenu;

    public Shop(String owner, int shopSize) {
        this.owner = owner;
        this.shopItems = new ArrayList<ShopItem>();
        this.shopMenu = new ShopMenu(this, shopSize);
        VendEx.playerShops.add(this);
    }

    public Shop(String owner, int shopSize, List<ShopItem> shopItems) {
        this.owner = owner;
        this.shopItems = shopItems;
        this.shopMenu = new ShopMenu(this, shopSize);
        VendEx.playerShops.add(this);
    }

    public String getOwner() {
        return owner;
    }

    public List<ShopItem> getShopItems() {
        return shopItems;
    }

    public void setShopItems(List<ShopItem> shopItems) {
        this.shopItems = shopItems;
    }

    public void addShopItem(ShopItem shopItem) {
        shopItems.add(shopItem);
        shopMenu.addMenuItem(shopItems.size() - 1, shopItem);
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    public void saveToFile(VendEx plugin) {
        File shopFile = new File(plugin.getDataFolder() + File.pathSeparator + "shops", owner + ".yml");
        FileConfiguration shopConfig = YamlConfiguration.loadConfiguration(shopFile);
        shopConfig.set("shopSize", shopMenu.getMenuSize());
        shopConfig.set("shopItems", null);
        for(int i = 0; i < shopItems.size(); i++) {
            shopConfig.set("shopItems." + i + ".price", shopItems.get(i).getItemPrice());
            shopConfig.set("shopItems." + i + ".amount", shopItems.get(i).getShopAmount());
            shopConfig.set("shopItems." + i + ".offerType", shopItems.get(i).getOfferType().toString());
            shopConfig.set("shopItems." + i + ".itemStack", shopItems.get(i).getRealStack());
        }
    }
}
