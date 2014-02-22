package net.shadowraze.vendex.trade.menus;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.trade.Trade;
import net.shadowraze.vendex.trade.TradeHandler;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class AddMoneyMenu extends Menu {

    private Inventory defaultMenu;
    private Map<String, Inventory> menuMap = new HashMap<String, Inventory>();

    private static ItemStack offerGoodStack = Util.metaStack("&6Offer: &aGOOD", "You have enough money for this offer", Material.STAINED_GLASS_PANE, DyeColor.LIME.getData());
    private static ItemStack offerBadStack = Util.metaStack("&6Offer: &cBAD", "You do not have enough money for this offer", Material.STAINED_GLASS_PANE, DyeColor.RED.getData());

    public AddMoneyMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
        MenuHandler.getInstance().registerMenu(ChatColor.stripColor(getTitle()), this);
    }

    @Override
    public void loadMenu() {
        defaultMenu = Bukkit.createInventory(null, getSize(), getTitle());
        for(int i = 0; i < getSize(); i++) {
            if(i == 0) defaultMenu.setItem(i, offerGoodStack);
            else if(i > 1 && i < 7) defaultMenu.setItem(i, digitStack(i, 0));
            else if(i == 8) defaultMenu.setItem(i, confirmStack(true, 0));
        }
    }

    @Override
    public void openMenu(Player player) {
        menuMap.put(player.getName(), Bukkit.createInventory(player, getSize(), getTitle()));
        menuMap.get(player.getName()).setContents(defaultMenu.getContents());
        player.openInventory(menuMap.get(player.getName()));
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if(e.getRawSlot() > 1 && e.getRawSlot() < 7) {
            if(e.getCurrentItem().getType() == Material.EMPTY_MAP) e.getInventory().setItem(e.getRawSlot(), digitStack(e.getSlot(), 1));
            else e.getInventory().setItem(e.getRawSlot(), digitStack(e.getSlot(), e.getCurrentItem().getAmount() == 9 ? 0 : e.getCurrentItem().getAmount() + 1));
            int moneyAmount = getMoneyAmount(e.getInventory());
            if(VendEx.economy.getBalance(e.getWhoClicked().getName()) >= moneyAmount) {
                e.getInventory().setItem(0, offerGoodStack);
                e.getInventory().setItem(8, confirmStack(true, moneyAmount));
            } else {
                e.getInventory().setItem(0, offerBadStack);
                e.getInventory().setItem(8, confirmStack(false, moneyAmount));
            }
        } else if(e.getRawSlot() == 8 && e.getInventory().getItem(0).isSimilar(offerGoodStack)) confirmAmount(e.getWhoClicked().getName());
    }

    @Override
    public void onMenuClose(final InventoryCloseEvent e) {
        final Trade playerTrade = TradeHandler.getInstance().getPlayerTrade(e.getPlayer().getName());
        if(playerTrade == null) return;
        Bukkit.getScheduler().runTaskLater(VendEx.getPlugin(), new Runnable() {
            @Override
            public void run() {
                e.getPlayer().openInventory(TradeHandler.TRADE_MENU.tradeMap.get(playerTrade));
            }
        }, 10L);
    }

    public void confirmAmount(String playerName) {
        Trade playerTrade = TradeHandler.getInstance().getPlayerTrade(playerName);
        if(playerTrade != null) playerTrade.setMoneyOffered(playerName, getMoneyAmount(menuMap.get(playerName)));
        Bukkit.getPlayerExact(playerName).closeInventory();
    }

    public int getMoneyAmount(Inventory menuInv) {
        return (menuInv.getItem(2).getType() == Material.EMPTY_MAP ? 0 : menuInv.getItem(2).getAmount() * 10000)
                + (menuInv.getItem(3).getType() == Material.EMPTY_MAP ? 0 : menuInv.getItem(3).getAmount() * 1000)
                + (menuInv.getItem(4).getType() == Material.EMPTY_MAP ? 0 : menuInv.getItem(4).getAmount() * 100)
                + (menuInv.getItem(5).getType() == Material.EMPTY_MAP ? 0 : menuInv.getItem(5).getAmount() * 10)
                + (menuInv.getItem(3).getType() == Material.EMPTY_MAP ? 0 : menuInv.getItem(6).getAmount());
    }

    public String digitString(int slotID) {
        if(slotID == 2) return "TEN THOUSANDS";
        else if(slotID == 3) return "THOUSANDS";
        else if(slotID == 4) return "HUNDREDS";
        else if(slotID == 5) return "TENS";
        else return "ONES";
    }

    public ItemStack digitStack(final int slotID, int amount) {
        ItemStack digitStack = Util.metaStack(digitString(slotID), "Representative of the " + digitString(slotID).toLowerCase() + " digit", amount > 0 ? Material.MAP : Material.EMPTY_MAP);
        digitStack.setAmount(amount > 0 ? amount : 1);
        return digitStack;
    }

    public ItemStack confirmStack(boolean canPay, int toPay) {
        if(canPay) return Util.metaStack("&aConfirm: " + "&6" + toPay + " " + VendEx.economy.currencyNamePlural(), "Click to offer this amount of money", Material.STATIONARY_WATER);
        else return Util.metaStack("&cCannot Confirm", "You do not have enough money", Material.STATIONARY_LAVA);
    }
}
