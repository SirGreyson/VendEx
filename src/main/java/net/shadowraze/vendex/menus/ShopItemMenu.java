package net.shadowraze.vendex.menus;

import net.shadowraze.vendex.market.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItemMenu implements Listener {

    private static Map<Inventory, ShopItem> inventoryMap = new HashMap<Inventory, ShopItem>();

    public static void openMenu(Player pViewer, ShopItem shopItem) {
        Inventory newMenu = newMenu(shopItem);
        inventoryMap.put(newMenu, shopItem);
        pViewer.openInventory(newMenu);
    }

    private static Inventory newMenu(ShopItem shopItem) {
        Inventory menuInv = Bukkit.createInventory(null, 9, "Offer Manager: " + shopItem.getRealStack().getType().toString());

        ItemStack loadItems = new ItemStack(Material.WATER_BUCKET);
        ItemMeta loadMeta = loadItems.getItemMeta();
        loadMeta.setDisplayName(ChatColor.GOLD + "Add Inventory");
        List<String> loadLore = new ArrayList<String>() {{
            add("Click to add items from your inventory");
        }};
        loadMeta.setLore(loadLore);
        loadItems.setItemMeta(loadMeta);

        ItemStack collectItems = new ItemStack(Material.BUCKET);
        ItemMeta collectMeta = collectItems.getItemMeta();
        collectMeta.setDisplayName(ChatColor.GOLD + "Collect Inventory");
        List<String> collectLore = new ArrayList<String>() {{
            add("Click to collect items from your shop inventory");
        }};
        collectMeta.setLore(collectLore);
        collectItems.setItemMeta(collectMeta);

        ItemStack removeOffer = new ItemStack(Material.LAVA_BUCKET);
        ItemMeta removeMeta = removeOffer.getItemMeta();
        removeMeta.setDisplayName(ChatColor.GOLD + "Remove Offer");
        List<String> removeLore = new ArrayList<String>() {{
            add("Click to remove this offer from your shop");
        }};
        removeMeta.setLore(removeLore);
        removeOffer.setItemMeta(removeMeta);

        menuInv.setItem(3, loadItems);
        menuInv.setItem(4, collectItems);
        menuInv.setItem(5, removeOffer);
        return menuInv;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if(!inventoryMap.containsKey(e.getInventory()) || e.getCurrentItem() == null) return;
        e.setCancelled(true);
    }
}
