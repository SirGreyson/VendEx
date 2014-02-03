package net.shadowraze.vendex.trade.oldTrade;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trade {

    private Player playerOne;
    private Player playerTwo;
    private Inventory tradeInventory;
    private boolean p1Accepted;
    private boolean p2Accepted;

    List<Integer> p1Slots;
    List<Integer> p2Slots;
    private Map<Integer, String> cmds;

    public Trade(Player playerOne, Player playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.p1Slots = new ArrayList<Integer>();
        this.p2Slots = new ArrayList<Integer>();
        this.cmds = new HashMap<Integer, String>();
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public Player[] getPlayers() {
        return new Player[] {playerOne, playerTwo};
    }

    public Map<Integer, String> getCmds() {
        return cmds;
    }

    public void openTradeMenu() {
        tradeInventory = Bukkit.createInventory(null, menuSize, menuTitle);
        for(int i = 0; i < menuSize; i++) {

        }
    }

    public void openTrade() {
        tradeInventory = Bukkit.createInventory(null, menuSize, menuTitle);
        int rowCount = tradeInventory.getSize() / 9;
        int currentSlot = 4;
        for(int i = 1; i < rowCount; i++) {
            tradeInventory.setItem(currentSlot, Util.metaStack("", new ArrayList<String>(), Material.PORTAL));
            currentSlot += (i == rowCount - 2 ? 5 : 9);
            if(i == (rowCount - 2))
                for(int j = currentSlot; j < currentSlot + 9; j++)
                    tradeInventory.setItem(j, Util.metaStack("", new ArrayList<String>(), Material.PORTAL));
            else if (i == (rowCount - 1)) tradeInventory.setItem(tradeInventory.getSize() - 5, Util.metaStack("", new ArrayList<String>(), Material.PORTAL));
        }
        tradeInventory.setItem(tradeInventory.getSize() - 9, Util.metaStack("Accept Trade", new ArrayList<String>() {{
            add("Click me to accept the current offer");
        }}, Material.WOOL, DyeColor.RED.getWoolData()));
        tradeInventory.setItem(tradeInventory.getSize() - 1, Util.metaStack("Accept Trade", new ArrayList<String>() {{
            add("Click me to accept the current offer");
        }}, Material.WOOL, DyeColor.RED.getWoolData()));
        tradeInventory.setItem(tradeInventory.getSize() - 8, Util.metaStack("Add Money", new ArrayList<String>() {{
            add("Click to add money to trade");
        }}, moneyItem));
        tradeInventory.setItem(tradeInventory.getSize() - 2, Util.metaStack("Add Money", new ArrayList<String>() {{
            add("Click to add money to trade");
        }}, moneyItem));
        cmds.put(tradeInventory.getSize() - 8, "trade add money");
        cmds.put(tradeInventory.getSize() - 2, "trade add money");
        playerOne.openInventory(tradeInventory);
        playerTwo.openInventory(tradeInventory);
    }
}
