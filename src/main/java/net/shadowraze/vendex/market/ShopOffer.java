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
            if(itemStack.getItemMeta().hasLore()) addAll(itemStack.getItemMeta().getLore());
            add("Click to  buy 1 for " + shopPrice);
        }}, itemStack.getType(), itemStack.getData().getData());

        return Util.metaStack(itemStack.getItemMeta().getDisplayName(), new ArrayList<String>() {{
            if(!inShop.isServerShop()) add(shopAmount + " left");
            add("Click to buy 1 for " + shopPrice);
            add("+Shift to buy " + itemStack.getMaxStackSize() + " for " + itemStack.getMaxStackSize() * shopPrice);
        }}, itemStack.getType(), itemStack.getData().getData());
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
