package net.shadowraze.vendex.trade;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trade {

    private String requester;
    private String requested;
    private Map<String, Boolean> hasAccepted;
    private Map<String, Integer> moneyOffered;
    private Map<String, Integer> gTokensOffered;

    public Trade(String requester, String requested) {
        this.requester = requester;
        this.requested = requested;
        this.hasAccepted = new HashMap<String, Boolean>();
        this.moneyOffered = new HashMap<String, Integer>();
        this.gTokensOffered = new HashMap<String, Integer>();
        TradeHandler.TRADE_MENU.openMenu(this);
    }

    public String getRequester() {
        return requester;
    }

    public String getRequested() {
        return requested;
    }

    public Player getPlayer(String playerName) {
        return Bukkit.getPlayerExact(playerName);
    }

    public boolean containsParticipant(String playerName) {
        return requester.equalsIgnoreCase(playerName) || requested.equalsIgnoreCase(playerName);
    }

    public boolean isRequester(String playerName) {
        return requester.equalsIgnoreCase(playerName);
    }

    public Boolean hasAccepted(String playerName) {
        if(!hasAccepted.containsKey(playerName)) hasAccepted.put(playerName, false);
        return hasAccepted.get(playerName);
    }

    public void setAccepted(String playerName, Boolean hasAccepted) {
        this.hasAccepted.put(playerName, hasAccepted);
        TradeHandler.TRADE_MENU.updateConfirmationItem(this, playerName);
        if(hasAccepted(requester) && hasAccepted(requested)) confirmTrade();
    }

    public boolean canAccept(String playerName) {
        if(Util.canAddItems(getPlayer(playerName).getInventory(), getTradeItems(!isRequester(playerName)))) return true;
        Messaging.sendErrorMessage(getPlayer(playerName), "You do not have enough free inventory space to accept this trade!");
        return false;
    }

    public void resetAccepted() {
        setAccepted(requester, false);
        setAccepted(requested, false);
    }

    public int getMoneyOffered(String playerName) {
        if(!moneyOffered.containsKey(playerName)) moneyOffered.put(playerName, 0);
        return moneyOffered.get(playerName);
    }

    public void setMoneyOffered(String playerName, int moneyAmount) {
        moneyOffered.put(playerName, moneyAmount);
        TradeHandler.TRADE_MENU.updateMoneyOfferItem(this, playerName);
    }

    public int getGTokensOffered(String playerName) {
        if(!gTokensOffered.containsKey(playerName)) gTokensOffered.put(playerName, 0);
        return gTokensOffered.get(playerName);
    }

    public void setGTokensOffered(String playerName, int gTokenAmount) {
        gTokensOffered.put(playerName, gTokenAmount);
        TradeHandler.TRADE_MENU.updateGTokenOfferItem(this, playerName);
    }

    public void saveTradeItems(String playerName, List<ItemStack> tradeItems) {
        for(int i = 0; i < tradeItems.size(); i++)
            VendEx.getPersistenceConfig().set("savedTradeItems." + playerName + ".tradeItems." + i, tradeItems.get(i));
        VendEx.savePersistenceConfig();
    }

    public List<ItemStack> getTradeItems(boolean isRequester) {
        return TradeHandler.TRADE_MENU.getTradeItems(this, isRequester);
    }

    public void giveTradeItems(Player toGive, String fromWho) {
        for(ItemStack tradeItem : getTradeItems(isRequester(fromWho)))
            toGive.getInventory().addItem(tradeItem);
        toGive.updateInventory();
        if(fromWho.equalsIgnoreCase(toGive.getName())) return;
        VendEx.economy.withdrawPlayer(fromWho, getMoneyOffered(fromWho));
        VendEx.economy.depositPlayer(toGive.getName(), getMoneyOffered(fromWho));
        if(VendEx.getVotingRewards() != null) {
            VendEx.getVotingRewards().getPlayer(fromWho).addGTokens(-getGTokensOffered(fromWho));
            VendEx.getVotingRewards().getPlayer(toGive.getName()).addGTokens(getGTokensOffered(fromWho));
        }
    }

    public void confirmTrade() {
        giveTradeItems(getPlayer(requester), requested);
        giveTradeItems(getPlayer(requested), requester);
        TradeHandler.getInstance().removeTrade(this);
        getPlayer(requester).closeInventory();
        getPlayer(requested).closeInventory();
        Messaging.sendMessage(getPlayer(requester), "&aTrade successful!");
        Messaging.sendMessage(getPlayer(requested), "&aTrade successful!");
    }

    public void cancelTrade(String playerName) {
        Player whoCancelled = getPlayer(playerName);
        Player otherPlayer = getPlayer(isRequester(playerName) ? requested : requester);
        if(whoCancelled != null) {
            Messaging.sendErrorMessage(whoCancelled, "You have cancelled the trade!");
            giveTradeItems(whoCancelled, playerName);
        } else if(whoCancelled == null) saveTradeItems(playerName, getTradeItems(isRequester(playerName)));
        Messaging.sendErrorMessage(otherPlayer, playerName + " has cancelled the trade!");
        giveTradeItems(otherPlayer, otherPlayer.getName());
        TradeHandler.getInstance().removeTrade(this);
        otherPlayer.getOpenInventory().close();
    }
}
