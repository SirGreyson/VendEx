package net.shadowraze.vendex.trade;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import org.amhokies.votingRewards.VotingRewards;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trade {

    private TradePlayer initiator;
    private TradePlayer invited;
    private Map<TradePlayer, List<Integer>> offerSlots;
    private Map<TradePlayer, List<TradeOffer>> tradeOffers;
    private Map<TradePlayer, Boolean> hasAccepted;
    private Map<TradePlayer, Integer> acceptButton;
    private Map<TradePlayer, Integer> moneyOffered;
    private Map<TradePlayer, Integer> gTokensOffered;
    private Inventory tradeInventory;
    private boolean isClosed;

    public Trade(String initiator, String invited) {
        this.initiator = new TradePlayer(initiator, this);
        this.invited = new TradePlayer(invited, this);
        this.offerSlots = new HashMap<TradePlayer, List<Integer>>();
        this.tradeOffers = new HashMap<TradePlayer, List<TradeOffer>>();
        this.hasAccepted = new HashMap<TradePlayer, Boolean>();
        this.acceptButton = new HashMap<TradePlayer, Integer>();
        this.moneyOffered = new HashMap<TradePlayer, Integer>();
        this.gTokensOffered = new HashMap<TradePlayer, Integer>();
        this.isClosed = false;
        loadTradeInventory();
    }

    //Initiator of Trade
    public TradePlayer getInitiator() {
        return initiator;
    }

    //Invited to Trade
    public TradePlayer getInvited() {
        return invited;
    }

    public TradePlayer getTradePlayer(String playerName) {
        if(initiator.getName().equalsIgnoreCase(playerName)) return initiator;
        else if(invited.getName().equalsIgnoreCase(playerName)) return invited;
        return null;
    }

    public TradePlayer[] getTradePlayers() {
        return new TradePlayer[] {initiator, invited};
    }

    public List<Integer> getOfferSlots(TradePlayer tradePlayer) {
        if(!offerSlots.containsKey(tradePlayer)) offerSlots.put(tradePlayer, new ArrayList<Integer>());
        return offerSlots.get(tradePlayer);
    }

    public List<TradeOffer> getTradeOffers(TradePlayer tradePlayer) {
        if(!tradeOffers.containsKey(tradePlayer)) tradeOffers.put(tradePlayer, new ArrayList<TradeOffer>());
        return tradeOffers.get(tradePlayer);
    }

    public void addTradeOffer(TradePlayer tradePlayer, TradeOffer tradeOffer) {
        if(tradeOffer.isMoney()) tradeInventory.setItem(moneyOffered.get(tradePlayer), currencyItem("MONEY", tradeOffer));
        else if(tradeOffer.isGoldTokens()) tradeInventory.setItem(gTokensOffered.get(tradePlayer), currencyItem("GTOKENS", tradeOffer));
        getTradeOffers(tradePlayer).add(tradeOffer);
        if(hasAccepted(initiator)) setHasAccepted(initiator, false);
        if(hasAccepted(invited)) setHasAccepted(invited, false);
    }

    private ItemStack currencyItem(String currencyType, TradeOffer tradeOffer) {
        if(currencyType.equalsIgnoreCase("MONEY")) return Util.metaStack("&6Money: &a" + tradeOffer.getCurrencyAmount(), new ArrayList<String>() {{
            add("Money offered");
        }}, TradeHandler.moneyItem);
        else return Util.metaStack("&6Gold Tokens: &a" + tradeOffer.getCurrencyAmount(), new ArrayList<String>() {{
            add("Gold Tokens offered");
        }}, TradeHandler.gTokenItem);
    }

    public void removeTradeOffer(TradePlayer tradePlayer, TradeOffer tradeOffer) {
        getTradeOffers(tradePlayer).remove(tradeOffer);
        if(hasAccepted(initiator)) setHasAccepted(initiator, false);
        if(hasAccepted(invited)) setHasAccepted(invited, false);
    }

    public Boolean hasAccepted(TradePlayer tradePlayer) {
        if(!hasAccepted.containsKey(tradePlayer)) hasAccepted.put(tradePlayer, false);
        return hasAccepted.get(tradePlayer);
    }

    private boolean canAcceptTrade(TradePlayer tradePlayer) {
        for(TradeOffer tradeOffer : getTradeOffers(tradePlayer == initiator ? invited : initiator))
            if(!Util.canAddItem(tradePlayer.getPlayer().getInventory(), tradeOffer.getItemStack())) {
                Messaging.sendErrorMessage(tradePlayer.getPlayer(), "You do not have enough free inventory space to accept this trade!");
                return false;
            }
        return true;
    }

    public void setHasAccepted(TradePlayer tradePlayer, final Boolean hasAccepted) {
        this.hasAccepted.put(tradePlayer, hasAccepted);
        tradeInventory.setItem(acceptButton.get(tradePlayer), Util.metaStack("&6Offer: " + (hasAccepted ? "&aACCEPTED" : "&cNOT ACCEPTED"), new ArrayList<String>() {{
            if(!hasAccepted) add("Click to accept the current offer");
            else add("You have accepted the current offer!");
        }}, Material.STAINED_GLASS_PANE, hasAccepted ? DyeColor.LIME.getData() : DyeColor.RED.getData()));
        if(hasAccepted && tradePlayer == initiator ? hasAccepted(invited) : hasAccepted(initiator)) confirmTrade();
    }

    public Inventory getTradeInventory() {
        return tradeInventory;
    }

    public void loadTradeInventory() {
        this.tradeInventory = Bukkit.createInventory(null, TradeHandler.tradeMenuSize, TradeHandler.tradeMenuTitle);
        tradeInventory.setContents(TradeHandler.defaultTradeMenu.getContents());
        for(int i = 0; i < tradeInventory.getSize() / 9; i++) {
            int slotCounter = 1;
            for(int j = i * 9; j < (i + 1) * 9; j++) {
                if(i < (tradeInventory.getSize() / 9) - 3) {
                    if(slotCounter <= 4) getOfferSlots(initiator).add(j);
                    else if(slotCounter <= 9) getOfferSlots(invited).add(j);
                } else if(slotCounter == 3 || slotCounter == 7) acceptButton.put(slotCounter == 3 ? initiator : invited, j);
                slotCounter++;
            }
        }
    }

    public void openTradeInventory() {
        initiator.getPlayer().openInventory(tradeInventory);
        invited.getPlayer().openInventory(tradeInventory);
    }

    public void closeTradeInventory() {
        if(initiator.getPlayer() != null && initiator.getPlayer().getOpenInventory() != null) initiator.getPlayer().closeInventory();
        if(invited.getPlayer() != null && invited.getPlayer().getOpenInventory() != null) invited.getPlayer().closeInventory();
    }

    public void cancelTrade(String whoCancelled) {
        String playerCancelled = whoCancelled + " has cancelled the trade";
        String selfCancelled = "You have cancelled the trade";
        this.isClosed = true;
        for(TradePlayer tradePlayer : tradeOffers.keySet()) {
            if(tradePlayer.getPlayer() != null) Messaging.sendErrorMessage(tradePlayer.getPlayer(), tradePlayer.getName().equalsIgnoreCase(whoCancelled) ? selfCancelled : playerCancelled);
            for(Integer slotID : getOfferSlots(tradePlayer))
                if(tradeInventory.getItem(slotID) != null) tradePlayer.getPlayer().getInventory().addItem(tradeInventory.getItem(slotID));
        }
        closeTradeInventory();
    }

    private void updateTradeOffers() {
        for(TradePlayer tradePlayer : offerSlots.keySet())
            for(Integer slotID : offerSlots.get(tradePlayer))
                if(tradeInventory.getItem(slotID) != null) addTradeOffer(tradePlayer, new TradeOffer(tradePlayer, tradeInventory.getItem(slotID)));
    }

    //TODO: Manage TradePlayers via HAshMAp and have set values of money / goldtokens offered instead of checking through shop offers 
    public void confirmTrade() {
        if(initiator.getPlayer() == null || invited.getPlayer() == null) cancelTrade(initiator.getPlayer() == null ? initiator.getName() : invited.getName());
        else {
            for(TradeOffer tradeOffer : getTradeOffers(initiator))
                if(tradeOffer.isMoney()) {
                    VendEx.economy.withdrawPlayer(initiator.getName(), tradeOffer.getCurrencyAmount());
                    VendEx.economy.depositPlayer(invited.getName(), tradeOffer.getCurrencyAmount());
                } else if(tradeOffer.isGoldTokens()) {
                    VendEx.getVotingRewards().getPlayer(initiator.getName()).addGTokens(-tradeOffer.getCurrencyAmount());
                    VendEx.getVotingRewards().getPlayer(invited.getName()).addGTokens(tradeOffer.getCurrencyAmount());
                } else invited.getPlayer().getInventory().addItem(tradeOffer.getItemStack());
            for(TradeOffer tradeOffer : getTradeOffers(invited))
                if(tradeOffer.isMoney()) {
                    VendEx.economy.withdrawPlayer(invited.getName(), tradeOffer.getCurrencyAmount());
                    VendEx.economy.depositPlayer(initiator.getName(), tradeOffer.getCurrencyAmount());
                } else if(tradeOffer.isGoldTokens()) {
                    VotingRewards.getPlayerManager().getPlayer(invited.getName()).addGTokens(-tradeOffer.getCurrencyAmount());
                    VotingRewards.getPlayerManager().getPlayer(initiator.getName()).addGTokens(tradeOffer.getCurrencyAmount());
                } else initiator.getPlayer().getInventory().addItem(tradeOffer.getItemStack());
            closeTradeInventory();
            Messaging.sendMessage(initiator.getPlayer(), "&aTrade successful!");
            Messaging.sendMessage(invited.getPlayer(), "&aTrade successful!");
        }
    }


    public void onClick(InventoryClickEvent e) {
        TradePlayer tradePlayer = getTradePlayer(e.getWhoClicked().getName());
        if(tradePlayer == null) {
            Messaging.sendErrorMessage((CommandSender) e.getWhoClicked(), "Something went wrong! Please report this to an admin!");
            cancelTrade(e.getWhoClicked().getName());
        } else {
            if(e.getRawSlot() < TradeHandler.tradeMenuSize && !getOfferSlots(tradePlayer).contains(e.getRawSlot())) e.setCancelled(true);
            if(getOfferSlots(tradePlayer).contains(e.getRawSlot())) updateTradeOffers();
            if(e.getRawSlot() == acceptButton.get(tradePlayer)) setHasAccepted(tradePlayer, !hasAccepted(tradePlayer));
            else if(e.getRawSlot() == TradeHandler.tradeMenuSize - 4) e.setCancelled(true);//TODO: open gold token menu
            else if(e.getRawSlot() == TradeHandler.tradeMenuSize - 5) cancelTrade(e.getWhoClicked().getName());
            else if(e.getRawSlot() == TradeHandler.tradeMenuSize - 6) e.setCancelled(true); //TODO: open money menu
        }
    }

    public void onClose(InventoryCloseEvent e) {
        if(isClosed) return;
        if(!hasAccepted(initiator) || !hasAccepted(invited))
            cancelTrade(e.getPlayer().getName());
    }
}
