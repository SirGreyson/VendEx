package net.shadowraze.vendex.trade;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TradePlayer {

    private String playerName;
    private Trade inTrade;
    private List<TradeOffer> tradeOffers;

    public TradePlayer(String playerName, Trade inTrade) {
        this.playerName = playerName;
        this.inTrade = inTrade;
        this.tradeOffers = new ArrayList<TradeOffer>();
    }

    public String getName() {
        return playerName;
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(playerName);
    }

    public Trade getInTrade() {
        return inTrade;
    }

    public List<TradeOffer> getTradeOffers() {
        return tradeOffers;
    }

    public void addTradeItem(TradeOffer tradeOffer) {
        tradeOffers.add(tradeOffer);
    }

    public void removeTradeItem(TradeOffer tradeOffer) {
        tradeOffers.remove(tradeOffer);
    }
}
