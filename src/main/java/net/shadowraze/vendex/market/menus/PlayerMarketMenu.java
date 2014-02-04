package net.shadowraze.vendex.market.menus;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.market.MarketManager;
import net.shadowraze.vendex.market.Shop;
import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerMarketMenu extends Menu {

    private static Map<Integer, Inventory> pageMap = new HashMap<Integer, Inventory>();
    private boolean intervalEnabled;
    private long intervalTicks;

    public PlayerMarketMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
        runTimer(VendEx.getPlugin());
    }

    @Override
    public void loadMenu() {
        double pageCount = Math.ceil(MarketManager.playerShops.size() / (getSize() - 9.00));
        List<Shop> shopList = new ArrayList<Shop>();
        for(int i = 6; i > 0; i--)
            shopList.addAll(MarketManager.shopsWithSize(i * 9));
        for(int i = 0; i < pageCount; i++) {
            pageMap.put(i, Bukkit.createInventory(null, getSize(), getTitle() + " - Page " + (i + 1)));
            for(int j = 0; j < 45 && shopList.size() > 0; j++) {
                pageMap.get(i).setItem(j, shopStack(shopList.get(0)));
                shopList.remove(0);
            }
            pageMap.get(i).setItem(getSize() - 9, Util.metaStack("Previous Page", new ArrayList<String>() {{
                add("Click to return to the previous page");
            }}, Material.BOOK_AND_QUILL));
            pageMap.get(i).setItem(getSize() - 1, Util.metaStack("Next Page", new ArrayList<String>() {{
                add("Click to go to the next page");
            }}, Material.BOOK_AND_QUILL));
        }
    }

    @Override
    public void openMenu(Player player) {

    }

    public void openMenu(Player player, int pageNumber) {
        player.openInventory(pageMap.get(pageNumber));
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        e.setCancelled(true);
        if(e.getSlot() < getSize() - 9) {
            if(!e.getCurrentItem().getItemMeta().getDisplayName().endsWith("'s Shop")) return;
            String shopOwner = e.getCurrentItem().getItemMeta().getDisplayName().split("'s Shop")[0];
            if(MarketManager.getPlayerShop(shopOwner) == null) Messaging.sendErrorMessage((Player) e.getWhoClicked(), "Error! That shop no longer exists!");
            else MenuHandler.SHOP_MENU.openMenu((Player) e.getWhoClicked(), MarketManager.getPlayerShop(shopOwner));
        } else if(e.getSlot() == getSize() - 9 || e.getSlot() == getSize() - 1) {
            Integer pageNumber = Integer.parseInt(e.getInventory().getName().split("- Page ")[1]) - 1;
            if(e.getSlot() == getSize() - 9 && pageNumber != 0)
                e.getWhoClicked().openInventory(pageMap.get(pageNumber - 1));
            else if(e.getSlot() == getSize() - 1 && pageNumber + 1 != pageMap.keySet().size())
                e.getWhoClicked().openInventory(pageMap.get(pageNumber + 1));
        }
    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {
        MenuHandler.VENDEX_MENU.openMenu((Player) e.getPlayer());
    }

    private void runTimer(final VendEx plugin) {
        intervalEnabled = plugin.getConfig().getBoolean("shopConfig.marketMenuReloadInterval.enabled");
        intervalTicks = plugin.getConfig().getInt("shopConfig.marketMenuReloadInterval.ticks");
        if(intervalEnabled) plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getLogger().info("Reloading Player Market Menu...");
                loadMenu();
                plugin.getLogger().info("Player Market Menu Loaded!");
            }
        }, intervalTicks, intervalTicks);
    }

    public void addShop(Shop newShop) {
        if(pageMap.get(pageMap.keySet().size() - 1).firstEmpty() == -1)
            pageMap.put(pageMap.keySet().size(), Bukkit.createInventory(null, getSize(), getTitle() + " - Page " + pageMap.keySet().size()));
        pageMap.get(pageMap.keySet().size() - 1).setItem(pageMap.get(pageMap.keySet().size() - 1).firstEmpty(), shopStack(newShop));
    }

    private ItemStack shopStack(Shop shop) {
        ItemStack shopStack = new ItemStack(shopSizeMaterial(shop.getShopInventory().getSize() / 9));
        ItemMeta stackMeta = shopStack.getItemMeta();
        stackMeta.setDisplayName(shop.getShopOwner() + "'s Shop");
        shopStack.setItemMeta(stackMeta);
        return shopStack;
    }

    private Material shopSizeMaterial(int shopSize) {
        switch(shopSize) {
            case 6 : return Material.DIAMOND_BLOCK;
            case 5: return Material.EMERALD_BLOCK;
            case 4: return Material.LAPIS_BLOCK;
            case 3: return Material.GOLD_BLOCK;
            case 2: return Material.IRON_BLOCK;
            case 1: return Material.LOG;
        }
        return Material.BEDROCK;
    }
}
