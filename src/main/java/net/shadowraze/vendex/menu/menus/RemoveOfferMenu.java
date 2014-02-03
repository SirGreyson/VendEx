package net.shadowraze.vendex.menu.menus;

import net.shadowraze.vendex.market.Shop;
import net.shadowraze.vendex.market.ShopOffer;
import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Variables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class RemoveOfferMenu extends Menu {

    private Map<String, Shop> shopMap = new HashMap<String, Shop>();

    public RemoveOfferMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
    }

    @Override
    public void loadMenu() {

    }

    @Override
    public void openMenu(Player player) {

    }

    public void openMenu(Player player, Shop shop) {
        shopMap.put(player.getName(), shop);
        Inventory remInv = Bukkit.createInventory(player,shop.getShopInventory().getSize(), getTitle());
        remInv.setContents(shop.getShopInventory().getContents());
        player.openInventory(remInv);
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        Player cPlayer = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if(!shopMap.containsKey(cPlayer.getName())) return;
        if(e.getCurrentItem() == null || e.getRawSlot() >= shopMap.get(cPlayer.getName()).getShopOffers().size()) return;
        ShopOffer shopOffer = shopMap.get(cPlayer.getName()).getShopOffers().get(e.getSlot());
        if(shopOffer.getShopAmount() > 0 && !shopMap.get(cPlayer.getName()).isServerShop()) Messaging.sendErrorMessage(cPlayer, Variables.ERRMESSAGES.get("offerCantDelete"));
        else {
            shopMap.get(cPlayer.getName()).removeShopOffer(shopOffer);
            cPlayer.closeInventory();
            Messaging.sendMessage(cPlayer, Variables.MESSAGES.get("offerRemoved"));
        }
    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {
        if(shopMap.containsKey(e.getPlayer().getName())) shopMap.remove(e.getPlayer().getName());
    }
}
