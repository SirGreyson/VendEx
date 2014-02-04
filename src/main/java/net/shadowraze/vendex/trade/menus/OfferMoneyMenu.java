package net.shadowraze.vendex.trade.menus;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.trade.TradePlayer;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OfferMoneyMenu extends Menu {

    private Inventory menuTemplate;
    private Map<TradePlayer, Inventory> inventoryMap;

    public OfferMoneyMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
        this.inventoryMap = new HashMap<TradePlayer, Inventory>();
    }

    private static ItemStack offerStatus(String offerStatus) {
        if(offerStatus.equalsIgnoreCase("GOOD")) return Util.metaStack("&6Offer: &aGOOD", new ArrayList<String>() {{
            add("You have enough money for this offer!");
        }}, Material.STAINED_GLASS_PANE, DyeColor.LIME.getData());
        else return Util.metaStack("&6Offer: &cBAD", new ArrayList<String>() {{
            add("You do not have enough money for this offer!");
        }}, Material.STAINED_GLASS_PANE, DyeColor.RED.getData());
    }

    private static String digitString(int slotID) {
        if(slotID == 3) return "HUNDREDS";
        else if(slotID == 4) return "TENS";
        else return "ONES";
    }

    private static ItemStack digitStack(final int slotID, int amount) {
        ItemStack digitStack = Util.metaStack(digitString(slotID), new ArrayList<String>() {{
            add("Representative of the " + digitString(slotID).toLowerCase() + " digit");
        }}, amount > 0 ? Material.MAP : Material.EMPTY_MAP);
        digitStack.setAmount(amount > 0 ? amount : 1);
        return digitStack;
    }

    private static ItemStack confirmStack(boolean canPay, int toPay) {
        if(canPay) return Util.metaStack("&aConfirm: " + "&6" + toPay + " " + VendEx.economy.currencyNamePlural(), new ArrayList<String>() {{
            add("Click to offer this amount of money");
        }}, Material.STATIONARY_WATER);
        else return Util.metaStack("&cCannot Confirm", new ArrayList<String>() {{
            add("You do not have enough money");
        }}, Material.STATIONARY_LAVA);
    }

    @Override
    public void loadMenu() {
        menuTemplate = Bukkit.createInventory(null, getSize(), getTitle());
        menuTemplate.setItem(0, offerStatus("GOOD"));
        menuTemplate.setItem(3, digitStack(3, 0));
        menuTemplate.setItem(4, digitStack(4, 0));
        menuTemplate.setItem(5, digitStack(5, 0));
        menuTemplate.setItem(8, confirmStack(true, 0));
    }

    @Override
    public void openMenu(Player player) {

    }

    public void openMenu(TradePlayer tradePlayer) {
        inventoryMap.put(tradePlayer, Bukkit.createInventory(null, getSize(), getTitle()));
        inventoryMap.get(tradePlayer).setContents(menuTemplate.getContents());
        tradePlayer.getPlayer().openInventory(inventoryMap.get(tradePlayer));
    }

    public int parseAmount(ItemStack[] valueStacks) {
        int hundredsValue = valueStacks[0].getType() == Material.EMPTY_MAP ? 0 : valueStacks[0].getAmount();
        int tensValue = valueStacks[1].getType() == Material.EMPTY_MAP ? 0 : valueStacks[1].getAmount();
        int onesValue = valueStacks[2].getType() == Material.EMPTY_MAP ? 0 : valueStacks[2].getAmount();
        return (hundredsValue * 100) + (tensValue * 10) + onesValue;
    }

    public void updateMenu(String playerName) {
        int playerMoney = (int) VendEx.economy.getBalance(playerName);
        Inventory menuInv = inventoryMap.get(playerName);
        int offerAmount = parseAmount(new ItemStack[] {menuInv.getItem(3), menuInv.getItem(4), menuInv.getItem(5)});
        if(offerAmount > playerMoney) {
            menuInv.setItem(0, offerStatus("BAD"));
            menuInv.setItem(8, confirmStack(false, playerMoney));
        } else {
            menuInv.setItem(0, offerStatus("GOOD"));
            menuInv.setItem(8, confirmStack(true, offerAmount));
        }
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        e.setCancelled(true);
        if(e.getRawSlot() > 2 && e.getRawSlot() < 6) {
            if(e.getCurrentItem().getType() == Material.EMPTY_MAP)
                e.getInventory().setItem(e.getSlot(), digitStack(e.getSlot(), 1));
            else if(e.getCurrentItem().getType() == Material.MAP)
                if(e.getCurrentItem().getAmount() >= 9) e.getInventory().setItem(e.getSlot(), digitStack(e.getSlot(), 0));
                else e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
            updateMenu(e.getWhoClicked().getName());
        } else if(e.getRawSlot() == 8 && e.getCurrentItem().getType() == Material.STATIONARY_WATER) {
            //TODO: Add money to trade
        }
    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {
    }
}
