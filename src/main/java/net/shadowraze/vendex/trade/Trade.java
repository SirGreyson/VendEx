package net.shadowraze.vendex.trade;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trade {

    Player playerOne;
    Player playerTwo;
    Inventory tradeMenu;
    Map<Integer, String> boundCmds;
    Map<String, List<Integer>> playerSlots;

    public Trade(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.boundCmds = new HashMap<Integer, String>();
        this.playerSlots = new HashMap<String, List<Integer>>();
        TradeHandler.getInstance().tradeMap.put(playerOne.getName(), this);
        TradeHandler.getInstance().tradeMap.put(playerTwo.getName(), this);
        openTrade();
    }

    public void addCmd(Integer slotID, String cmd) {
        boundCmds.put(slotID, cmd);
    }

    public void addPlayerSlot(String playerName, Integer slotID) {
        if(playerSlots.containsKey(playerName)) playerSlots.put(playerName, new ArrayList<Integer>());
        playerSlots.get(playerName).add(slotID);
    }

    public void openTrade() {
        tradeMenu.setContents(TradeHandler.getInstance().getTradeMenuTemplate().getContents());
        playerOne.openInventory(tradeMenu);
        playerTwo.openInventory(tradeMenu);
    }
}
