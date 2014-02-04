package net.shadowraze.vendex.market.menus;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.market.MarketManager;
import net.shadowraze.vendex.market.Shop;
import net.shadowraze.vendex.market.ShopOffer;
import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Variables;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AddOfferMenu extends Menu implements Listener {

    private Map<String, Shop> shopMap = new HashMap<String, Shop>();
    private Map<String, ItemStack> addOfferMap = new HashMap<String, ItemStack>();
    private Map<String, ShopOffer> addOfferCmd = new HashMap<String, ShopOffer>();


    public AddOfferMenu(VendEx plugin, String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void loadMenu() {

    }

    @Override
    public void openMenu(Player player) {

    }

    public void openMenu(Player player, Shop addShop) {
        shopMap.put(player.getName(), addShop);
        Inventory menuInv = Bukkit.createInventory(player, getSize(), getTitle());
        player.openInventory(menuInv);
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        Player cPlayer = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if (!shopMap.containsKey(cPlayer.getName()) || shopMap.get(cPlayer.getName()) == null) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (MarketManager.getBlackList().contains(e.getCurrentItem().getType())) {
            if (!VendEx.permission.playerHas(cPlayer, Variables.ADMIN_PERM)) {
                Messaging.sendErrorMessage(cPlayer, Variables.ERRMESSAGES.get("blackListItem"));
                return;
            }
        }
        for (ShopOffer shopOffer : shopMap.get(cPlayer.getName()).getShopOffers())
            if (shopOffer.getItemStack().isSimilar(e.getCurrentItem())) {
                Messaging.sendErrorMessage(cPlayer, Variables.ERRMESSAGES.get("offerAlreadyExists"));
                return;
            }
        cPlayer.closeInventory();
        ItemStack addStack = new ItemStack(e.getCurrentItem());
        addStack.setAmount(1);
        addOfferMap.put(cPlayer.getName(), addStack);
        Messaging.sendMessage(cPlayer, Variables.MESSAGES.get("askOfferPrice"));
    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if(!shopMap.containsKey(e.getPlayer().getName())) return;
        Shop pShop = shopMap.get(e.getPlayer().getName());
        e.setCancelled(true);
        if(addOfferCmd.containsKey(e.getPlayer().getName())) {
            if(!e.getMessage().equalsIgnoreCase("noCmd")) addOfferCmd.get(e.getPlayer().getName()).setBoundCmd(e.getMessage());
            pShop.addShopOffer(addOfferCmd.get(e.getPlayer().getName()));
            addOfferCmd.remove(e.getPlayer().getName());
            Messaging.sendMessage(e.getPlayer(), Variables.MESSAGES.get("offerAdded"));
        } else if (addOfferMap.containsKey(e.getPlayer().getName())) {
            try {
                Integer itemPrice = Integer.parseInt(e.getMessage());
                if(pShop.isServerShop() && VendEx.permission.playerHas(e.getPlayer(), Variables.ADMIN_PERM)) {
                    Messaging.sendMessage(e.getPlayer(), Variables.MESSAGES.get("askOfferCmd"));
                    addOfferCmd.put(e.getPlayer().getName(), new ShopOffer(pShop, addOfferMap.get(e.getPlayer().getName()), itemPrice));
                    addOfferMap.remove(e.getPlayer().getName());
                    return;
                }
                pShop.addShopOffer(new ShopOffer(pShop, addOfferMap.get(e.getPlayer().getName()), itemPrice, 0));
                addOfferMap.remove(e.getPlayer().getName());
                Messaging.sendMessage(e.getPlayer(), Variables.MESSAGES.get("offerAdded"));
            } catch (NumberFormatException exc) {
                Messaging.sendErrorMessage(e.getPlayer(), Variables.ERRMESSAGES.get("entryNotNumber"));
            }
        }
        shopMap.remove(e.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(shopMap.containsKey(e.getPlayer().getName())) shopMap.remove(e.getPlayer().getName());
        if(addOfferMap.containsKey(e.getPlayer().getName())) addOfferMap.remove(e.getPlayer().getName());
    }
}
