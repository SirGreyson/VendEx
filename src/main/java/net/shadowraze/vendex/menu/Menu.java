package net.shadowraze.vendex.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public abstract class Menu {

    private String menuTitle;
    private int menuSize;

    public Menu(){
    }

    public Menu(String menuTitle, int menuSize) {
        this.menuTitle = menuTitle;
        this.menuSize = menuSize;
        loadMenu();
    }

    public String getTitle() {
        return menuTitle;
    }

    public void setTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public int getSize() {
        return menuSize;
    }

    public void setSize(int menuSize) {
        this.menuSize = menuSize;
    }

    public abstract void loadMenu();

    public abstract void openMenu(Player player);

    public abstract void onMenuClick(InventoryClickEvent e);

    public abstract void onMenuClose(InventoryCloseEvent e);
}
