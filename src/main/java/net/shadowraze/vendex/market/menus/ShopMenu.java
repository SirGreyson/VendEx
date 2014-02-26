package net.shadowraze.vendex.market.menus;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.market.Shop;
import net.shadowraze.vendex.market.ShopOffer;
import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import net.shadowraze.vendex.util.Variables;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ShopMenu extends Menu {

    private static Map<String, Shop> shopMap = new HashMap<String, Shop>();

    @Override
    public void loadMenu() {
    }

    @Override
    public void openMenu(Player player) {
    }

    public void openMenu(Player player, Shop shop) {
        shopMap.put(player.getName(), shop);
        player.openInventory(shop.getShopInventory());
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        Player cPlayer = (Player) e.getWhoClicked();
        Shop cShop = shopMap.get(cPlayer.getName());
        e.setCancelled(true);
        if(e.getClick() != ClickType.LEFT && e.getClick() != ClickType.SHIFT_LEFT) return;
        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getRawSlot() >= cShop.getShopOffers().size()) return;
        ShopOffer shopOffer = cShop.getShopOffers().get(e.getSlot());
        if(cPlayer.getName().equalsIgnoreCase(cShop.getShopOwner())) MenuHandler.INVENTORY_MENU.openMenu(cPlayer, shopOffer);
        else {
            int buyAmount = e.getClick() == ClickType.LEFT ? 1 : shopOffer.getItemStack().getMaxStackSize();
            if(VendEx.economy.getBalance(cPlayer.getName()) >= buyAmount * shopOffer.getShopPrice()) {
                if(shopOffer.hasBoundCmd() && shopOffer.getShop().isServerShop()) {
                    VendEx.economy.withdrawPlayer(cPlayer.getName(), shopOffer.getShopPrice() * buyAmount);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), shopOffer.getBoundCmd().replace("%p", cPlayer.getName()));
                } else {
                    if(shopOffer.getShopAmount() >= buyAmount) {
                        ItemStack buyStack = new ItemStack(shopOffer.getItemStack());
                        buyStack.setAmount(buyAmount);
                        if(Util.canAddItem(cPlayer.getInventory(), buyStack)) {
                            VendEx.economy.withdrawPlayer(cPlayer.getName(), shopOffer.getShopPrice() * buyAmount);
                            VendEx.economy.depositPlayer(cShop.getShopOwner(), shopOffer.getShopPrice() * buyAmount);
                            shopOffer.setShopAmount(shopOffer.getShopAmount() - buyAmount);
                            cPlayer.getInventory().addItem(buyStack);
                            if(Bukkit.getPlayerExact(cShop.getShopOwner()) != null)
                                Messaging.sendMessage(Bukkit.getPlayerExact(cShop.getShopOwner()), cPlayer.getName() + " has just spent " + shopOffer.getShopPrice() * buyAmount + " at your shop!");
                        } else Messaging.sendErrorMessage(cPlayer, Variables.ERRMESSAGES.get("cannotHoldPurchase"));
                    } else Messaging.sendErrorMessage(cPlayer, Variables.ERRMESSAGES.get("itemOutOfStock"));
                }
            } else Messaging.sendErrorMessage(cPlayer, Variables.ERRMESSAGES.get("notEnoughMoney"));
        }
    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {
        shopMap.remove(e.getPlayer().getName());
        //TODO: Open player market
    }
}
