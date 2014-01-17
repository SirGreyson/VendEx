package net.shadowraze.vendex.menus;

import net.shadowraze.vendex.market.OfferType;
import net.shadowraze.vendex.market.Shop;
import net.shadowraze.vendex.market.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu implements Listener {

    private Shop menuShop;
    private int menuSize;
    private Inventory menuInventory;

    public ShopMenu(Shop menuShop, int menuSize) {
        this.menuShop = menuShop;
        this.menuSize = menuSize;
        this.menuInventory = Bukkit.createInventory(null, menuSize, menuShop.getOwner() + "'s Shop");
    }

    public Shop getMenuShop() {
        return menuShop;
    }

    public int getMenuSize() {
        return menuSize;
    }

    public Inventory getMenuInventory() {
        return menuInventory;
    }

    public void loadInventory() {
        for(int i = 0; i < menuShop.getShopItems().size(); i ++)
            addMenuItem(i, menuShop.getShopItems().get(i));
    }

    public void addMenuItem(int slotIndex, ShopItem shopItem) {
        ItemStack menuItem = new ItemStack(shopItem.getRealStack());
        ItemMeta menuMeta = menuItem.getItemMeta();
        menuMeta.setLore(menuItemLore(shopItem));
        menuItem.setItemMeta(menuMeta);
        menuInventory.setItem(slotIndex, menuItem);
    }

    public void removeMenuItem(int slotIndex) {
        menuInventory.setItem(slotIndex, null);
        loadInventory();
    }

    private List<String> menuItemLore(ShopItem shopItem) {
        List<String> itemInfo = new ArrayList<String>();
        itemInfo.add(shopItem.getShopAmount() + " left");
        itemInfo.add("Click to " + OfferType.getString(shopItem.getOfferType()) + " 1 for " + shopItem.getItemPrice());
        itemInfo.add("+Shift to " + OfferType.getString(shopItem.getOfferType()) + " " + shopItem.getRealStack().getMaxStackSize()
                    + " for " + shopItem.getItemPrice() * shopItem.getRealStack().getMaxStackSize());
        return itemInfo;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if(e.getInventory() != menuInventory || e.getCurrentItem() == null) return;
        ShopItem shopItem = menuShop.getShopItems().get(e.getSlot());
        e.setCancelled(true);
    }
}
