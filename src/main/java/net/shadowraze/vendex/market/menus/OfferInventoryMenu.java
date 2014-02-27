package net.shadowraze.vendex.market.menus;

import net.shadowraze.vendex.market.ShopOffer;
import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class OfferInventoryMenu extends Menu {

    private static Map<String, ShopOffer> offerMap = new HashMap<String, ShopOffer>();

    public OfferInventoryMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
    }

    @Override
    public void loadMenu() {
    }

    @Override
    public void openMenu(Player player) {
    }

    public void openMenu(Player player, ShopOffer shopOffer) {
        offerMap.put(player.getName(), shopOffer);
        Inventory offerInv = Bukkit.createInventory(null, getSize(), getTitle());
        ItemStack invStack = new ItemStack(shopOffer.getItemStack());
        int amntLeft = shopOffer.getShopAmount();
        for (int i = 0; i < offerInv.getSize() && amntLeft > 0; i++) {
            invStack.setAmount(amntLeft > shopOffer.getItemStack().getMaxStackSize()? shopOffer.getItemStack().getMaxStackSize() : amntLeft);
            offerInv.setItem(i, invStack);
            amntLeft -= invStack.getAmount();
        } while(amntLeft > 0) {
            invStack.setAmount(amntLeft > invStack.getMaxStackSize() ? invStack.getMaxStackSize() : amntLeft);
            player.getWorld().dropItemNaturally(player.getLocation(), invStack);
            amntLeft -= 1;
            if(amntLeft == 0) Messaging.sendErrorMessage(player, "You had an impossible stack! This menu has been fixed and remaining items have been dropped!");
        }
        invStack.setAmount(shopOffer.getShopAmount());
        player.openInventory(offerInv);
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        Player cPlayer = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(!offerMap.containsKey(cPlayer.getName()) || e.getCurrentItem() == null) return;
        if(e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().isSimilar(offerMap.get(cPlayer.getName()).getItemStack())) e.setCancelled(false);
    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {
        offerMap.get(e.getPlayer().getName()).setShopAmount(inventoryAmount(e.getInventory(), offerMap.get(e.getPlayer().getName())));
        //MenuHandler.SHOP_MENU.openMenu((Player) e.getPlayer(), offerMap.get(e.getPlayer().getName()).getShop());
        offerMap.remove(e.getPlayer().getName());
    }

    private int inventoryAmount(Inventory inventory, ShopOffer shopOffer) {
        int total = 0;
        for(ItemStack itemStack : inventory.getContents())
            if(itemStack != null && itemStack.isSimilar(shopOffer.getItemStack())) total += itemStack.getAmount();
        return total;
    }
}
