package net.shadowraze.vendex.market.menus;

import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class ShopManagementMenu extends Menu {

    private Inventory menuInventory;
    private Map<Integer, String> menuCmds;

    public ShopManagementMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
    }

    @Override
    public void loadMenu() {
        menuInventory = Bukkit.createInventory(null, getSize(), getTitle());
        menuCmds = new HashMap<Integer, String>();
        for(int i = 0; i < getSize(); i++) {
            ConfigurationSection iSec = MenuHandler.menuConfig.getConfigurationSection("manageShopMenu.menuItems." + i);
            if(iSec != null) {
                menuInventory.setItem(i, Util.metaStack(iSec.getString("title"), iSec.getStringList("lore"), Material.valueOf(iSec.getString("material"))));
                menuCmds.put(i, iSec.getString("cmd"));
            }
        }
    }

    @Override
    public void openMenu(Player player) {
        player.openInventory(menuInventory);
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        Player cPlayer = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(menuCmds.containsKey(e.getRawSlot())) cPlayer.performCommand(menuCmds.get(e.getSlot()));
    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {
    }
}
