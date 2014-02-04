package net.shadowraze.vendex.tradeFinal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
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

    public Boolean hasAccepted(String playerName) {
        if(!hasAccepted.containsKey(playerName)) hasAccepted.put(playerName, false);
        return hasAccepted.get(playerName);
    }

    public void setAccepted(String playerName, Boolean hasAccepted) {
        this.hasAccepted.put(playerName, hasAccepted);
        //TODO: Update the confirmation item
    }

    public int getMoneyOffered(String playerName) {
        if(!moneyOffered.containsKey(playerName)) moneyOffered.put(playerName, 0);
        return moneyOffered.get(playerName);
    }

    public void setMoneyOffered(String playerName, int moneyAmount) {
        moneyOffered.put(playerName, moneyAmount);
    }

    public int getGTokensOffered(String playerName) {
        if(!gTokensOffered.containsKey(playerName)) gTokensOffered.put(playerName, 0);
        return gTokensOffered.get(playerName);
    }

    public void setGTokensOffered(String playerName, int gTokenAmount) {
        gTokensOffered.put(playerName, gTokenAmount);
    }
}
