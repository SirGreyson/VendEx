package net.shadowraze.vendex.market;

import net.shadowraze.vendex.util.Util;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ShopOffer {

    private Shop inShop;
    private ItemStack itemStack;
    private int shopAmount;
    private int shopPrice;
    private String boundCmd;

    public ShopOffer(Shop inShop, ItemStack itemStack, int shopPrice) {
        this.inShop = inShop;
        this.itemStack = itemStack;
        this.shopPrice = shopPrice;
    }

    public ShopOffer(Shop inShop, ItemStack itemStack, int shopPrice, int shopAmount) {
        this.inShop = inShop;
        this.itemStack = itemStack;
        this.shopPrice = shopPrice;
        this.shopAmount = shopAmount;
    }

    public ShopOffer(Shop inShop, ItemStack itemStack, int shopPrice, String boundCmd) {
        this.inShop = inShop;
        this.itemStack = itemStack;
        this.shopPrice = shopPrice;
        this.boundCmd = boundCmd;
    }

    public Shop getShop() {
        return inShop;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemStack getMenuStack() {
        if(hasBoundCmd()) return Util.metaStack(itemStack.getItemMeta().getDisplayName(), new ArrayList<String>() {{
            if(itemStack.getItemMeta().hasLore()) add(" ");
            if(itemStack.getItemMeta().hasLore()) addAll(itemStack.getItemMeta().getLore());
            add("Click to  buy 1 for " + shopPrice);
        }}, itemStack);

        return Util.metaStack(itemStack.getItemMeta().getDisplayName(), new ArrayList<String>() {{
            if(itemStack.getItemMeta().hasLore()) add(" ");
            if(!inShop.isServerShop()) add(shopAmount > 0 ? "&6" + shopAmount + " left" : "&cOUT OF STOCK");
            add("&aClick to buy &e1 &afor &e" + shopPrice);
            add("&e+Shift &ato buy &e" + itemStack.getMaxStackSize() + " &afor&e " + itemStack.getMaxStackSize() * shopPrice);
        }}, itemStack);
    }

    public int getShopPrice() {
        return shopPrice;
    }

    public int getShopAmount() {
        if(inShop.isServerShop()) return Integer.MAX_VALUE;
        else return shopAmount;
    }

    public void setShopAmount(int shopAmount) {
        if(inShop.isServerShop()) return;
        this.shopAmount = shopAmount;
        inShop.updateShopOffer(this);
    }

    public String getBoundCmd() {
        return boundCmd;
    }

    public void setBoundCmd(String boundCmd) {
        this.boundCmd = boundCmd;
    }

    public boolean hasBoundCmd() {
        if(boundCmd == null) return false;
        return true;
    }
}
