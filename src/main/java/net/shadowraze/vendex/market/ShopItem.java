package net.shadowraze.vendex.market;

import org.bukkit.inventory.ItemStack;

public class ShopItem {

    private Shop itemShop;
    private ItemStack realStack;
    private OfferType offerType;
    private int itemPrice;
    private int shopAmount;

    public ShopItem(Shop itemShop, ItemStack realStack, OfferType offerType, int itemPrice) {
        this.itemShop = itemShop;
        this.realStack = realStack;
        this.offerType = offerType;
        this.itemPrice = itemPrice;
        itemShop.addShopItem(this);
    }

    public ShopItem(Shop itemShop, ItemStack realStack, OfferType offerType, int itemPrice, int shopAmount) {
        this.itemShop = itemShop;
        this.realStack = realStack;
        this.offerType = offerType;
        this.itemPrice = itemPrice;
        this.shopAmount = shopAmount;
        itemShop.addShopItem(this);
    }

    public Shop getItemShop() {
        return itemShop;
    }

    public ItemStack getRealStack() {
        return realStack;
    }

    public OfferType getOfferType() {
        return offerType;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int newPrice) {
        this.itemPrice = newPrice;
    }

    public int getShopAmount() {
        return shopAmount;
    }

    public void setShopAmount(int newAmount) {
        this.shopAmount = newAmount;
    }
}
