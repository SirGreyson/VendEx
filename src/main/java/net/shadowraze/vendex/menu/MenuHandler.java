package net.shadowraze.vendex.menu;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.market.MarketManager;
import net.shadowraze.vendex.market.menus.*;
import net.shadowraze.vendex.menu.menus.AdminMenu;
import net.shadowraze.vendex.menu.menus.VendExMenu;
import net.shadowraze.vendex.trade.TradeHandler;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;
import java.util.Map;

public class MenuHandler implements Listener {

    private static MenuHandler instance = new MenuHandler();
    public static ConfigurationSection menuConfig = VendEx.getPlugin().getConfig().getConfigurationSection("menuConfig");
    private static Map<String, Menu> menuMap = new HashMap<String, Menu>();

    public static VendExMenu VENDEX_MENU = new VendExMenu(Util.parseColor(menuConfig.getString("vendExMenu.title")), menuConfig.getInt("vendExMenu.size"));
    public static AdminMenu ADMIN_MENU = new AdminMenu(Util.parseColor(menuConfig.getString("adminMenu.title")), menuConfig.getInt("adminMenu.size"));
    public static ShopMenu SHOP_MENU = new ShopMenu();
    public static ShopCreationMenu CREATE_SHOP_MENU = new ShopCreationMenu(Util.parseColor(menuConfig.getString("createShopMenu.title")), menuConfig.getInt("createShopMenu.size"));
    public static ShopManagementMenu MANAGE_SHOP_MENU = new ShopManagementMenu(Util.parseColor(menuConfig.getString("manageShopMenu.title")), menuConfig.getInt("manageShopMenu.size"));
    public static ServerShopManagementMenu MANAGE_SERVER_SHOP_MENU = new ServerShopManagementMenu(Util.parseColor(menuConfig.getString("manageServerShopMenu.title")), menuConfig.getInt("manageServerShopMenu.size"));
    public static AddOfferMenu ADD_OFFER_MENU = new AddOfferMenu(VendEx.getPlugin(), Util.parseColor(menuConfig.getString("addOfferMenu.title")), menuConfig.getInt("addOfferMenu.size"));
    public static RemoveOfferMenu REMOVE_OFFER_MENU = new RemoveOfferMenu(Util.parseColor(menuConfig.getString("removeOfferMenu.title")), 9);
    public static OfferInventoryMenu INVENTORY_MENU = new OfferInventoryMenu(Util.parseColor(menuConfig.getString("offerInventoryMenu.title")), menuConfig.getInt("offerInventoryMenu.size"));
    public static PlayerMarketMenu MARKET_MENU = new PlayerMarketMenu(Util.parseColor(menuConfig.getString("playerMarketMenu.title")), menuConfig.getInt("playerMarketMenu.size"));

    public static MenuHandler getInstance() {
        return instance;
    }

    public void loadMenus(VendEx plugin) {
        registerMenu(ChatColor.stripColor(VENDEX_MENU.getTitle()), VENDEX_MENU);
        registerMenu(ChatColor.stripColor(ADMIN_MENU.getTitle()), ADMIN_MENU);
        registerMenu(ChatColor.stripColor(CREATE_SHOP_MENU.getTitle()), CREATE_SHOP_MENU);
        registerMenu(ChatColor.stripColor(MANAGE_SHOP_MENU.getTitle()), MANAGE_SHOP_MENU);
        registerMenu(ChatColor.stripColor(MANAGE_SERVER_SHOP_MENU.getTitle()), MANAGE_SERVER_SHOP_MENU);
        registerMenu(ChatColor.stripColor(ADD_OFFER_MENU.getTitle()), ADD_OFFER_MENU);
        registerMenu(ChatColor.stripColor(REMOVE_OFFER_MENU.getTitle()), REMOVE_OFFER_MENU);
        registerMenu(ChatColor.stripColor(INVENTORY_MENU.getTitle()), INVENTORY_MENU);
        registerMenu(ChatColor.stripColor(MARKET_MENU.getTitle()), MARKET_MENU);
    }

    public static boolean isMenuInventory(String inventoryTitle) {
        String invTitle = ChatColor.stripColor(inventoryTitle);
        if(menuMap.containsKey(invTitle)) return true;
        return false;
    }

    public void registerMenu(String title, Menu newMenu) {
        menuMap.put(title, newMenu);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        String invTitle = ChatColor.stripColor(e.getInventory().getTitle());
        if(invTitle.endsWith("'s Shop") || e.getInventory().getTitle().equalsIgnoreCase(MarketManager.serverShop.getShopInventory().getTitle())) SHOP_MENU.onMenuClick(e);
        else if(invTitle.startsWith(ChatColor.stripColor(MARKET_MENU.getTitle()))) MARKET_MENU.onMenuClick(e);
        if(!menuMap.containsKey(invTitle)) return;
        if(menuMap.get(invTitle).getSize() != e.getInventory().getSize() && !invTitle.equalsIgnoreCase(ChatColor.stripColor(REMOVE_OFFER_MENU.getTitle()))) return;
        menuMap.get(invTitle).onMenuClick(e);
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e) {
        String invTitle = ChatColor.stripColor(e.getInventory().getTitle());
        if(invTitle.endsWith("'s Shop")) SHOP_MENU.onMenuClose(e);
        else if(invTitle.startsWith(MARKET_MENU.getTitle())) MARKET_MENU.onMenuClose(e);
        else if(invTitle.startsWith(ChatColor.stripColor(TradeHandler.TRADE_MENU.getTitle())) && e.getPlayer().getItemOnCursor() != null) {
            if(e.getPlayer().getItemOnCursor().getType() != Material.AIR) e.getPlayer().getInventory().addItem(e.getPlayer().getItemOnCursor());
            e.getPlayer().setItemOnCursor(null);
        }
        if(!menuMap.containsKey(invTitle)) return;
        if(menuMap.get(invTitle).getSize() != e.getInventory().getSize()) return;
        menuMap.get(invTitle).onMenuClose(e);
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent e) {
        String invTitle = ChatColor.stripColor(e.getInventory().getTitle());
        if(!menuMap.containsKey(invTitle)) return;
        e.setCancelled(true);
    }

    public static void closeAllInventories() {
        VendEx.getPlugin().getLogger().info("Closing all open inventories...");
        for(Player player : Bukkit.getServer().getOnlinePlayers())
            if(player.getOpenInventory() != null) player.closeInventory();
    }
}