package net.shadowraze.vendex.trade;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TradeOffer {

    private TradePlayer tradePlayer;
    private ItemStack itemStack;
    private boolean isMoney;
    private boolean isGoldTokens;
    private int currencyAmount;

    public TradeOffer(TradePlayer tradePlayer, ItemStack itemStack) {
        this.tradePlayer = tradePlayer;
        this.itemStack = itemStack;
    }

    public TradeOffer(TradePlayer tradePlayer, ItemStack itemStack, boolean isMoney, boolean isGoldTokens, int currencyAmount) {
        this.tradePlayer = tradePlayer;
        this.itemStack = itemStack;
        this.isMoney = isMoney;
        this.isGoldTokens = isGoldTokens;
        this.currencyAmount = currencyAmount;
    }

    public TradePlayer getTradePlayer() {
        return tradePlayer;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isMoney() {
        return isMoney;
    }

    public boolean isGoldTokens() {
        return isGoldTokens;
    }

    public int getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(int currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public static ItemStack moneyItem(int moneyAmount) {
        return Util.metaStack(ChatColor.GOLD + "" + moneyAmount + " " + VendEx.economy.currencyNamePlural(), new ArrayList<String>(), TradeHandler.moneyItem);
    }

    public static ItemStack gTokeItem(int gTokenAmount) {
        return Util.metaStack(ChatColor.GOLD + "" + gTokenAmount + " Gold Tokens", new ArrayList<String>(), TradeHandler.gTokenItem);
    }
}
