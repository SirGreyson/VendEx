package net.shadowraze.vendex.menu.menus;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import net.shadowraze.vendex.util.Variables;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class AdminMenu extends Menu {

    private Inventory menuInventory;
    private Boolean viewAsAdmin;
    private Map<Integer, String> menuCmds;

    public AdminMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
    }

    @Override
    public void loadMenu() {
        if(menuInventory == null) menuInventory = Bukkit.createInventory(null, getSize(), getTitle());
        if(viewAsAdmin == null) viewAsAdmin = true;
        menuInventory.clear();
        menuCmds = new HashMap<Integer, String>();
        for (int i = 0; i < getSize(); i++) {
            ConfigurationSection iSec = MenuHandler.menuConfig.getConfigurationSection((viewAsAdmin ? "adminMenu." : "vendExMenu.") + "menuItems." + i);
            if (iSec != null) {
                menuInventory.setItem(i, Util.metaStack(iSec.getString("title"), iSec.getStringList("lore"), Material.valueOf(iSec.getString("material"))));
                menuCmds.put(i, iSec.getString("cmd"));
            }
            ConfigurationSection aSec = MenuHandler.menuConfig.getConfigurationSection("adminMenu.menuItems." + (getSize() - 1));
            if(aSec != null) {
                menuInventory.setItem((getSize() - 1), Util.metaStack(aSec.getString("title"), aSec.getStringList("lore"), Material.valueOf(aSec.getString("material"))));
                menuCmds.put((getSize() - 1), aSec.getString("cmd"));
            }
        }
    }

    public void toggleMenu() {
        this.viewAsAdmin = !viewAsAdmin;
        for(HumanEntity player : menuInventory.getViewers())
            Messaging.sendMessage((Player) player, ChatColor.RED + "Admin View: " + ChatColor.AQUA + (viewAsAdmin ? "Enabled" : "Disabled"));
        loadMenu();
    }

    @Override
    public void openMenu(Player player) {
        if(VendEx.permission.playerHas(player, Variables.ADMIN_PERM)) player.openInventory(menuInventory);
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
