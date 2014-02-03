package net.shadowraze.vendex.trade;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class TradeHandler implements Listener {

    private static TradeHandler instance = new TradeHandler();

    public Map<String, Trade> tradeMap = new HashMap<String, Trade>();
    public Map<String, String> inviteMap = new HashMap<String, String>();
    public Map<String, Long> inviteTime = new HashMap<String, Long>();

    public Map<String, Boolean> isAcceptingTrades = new HashMap<String, Boolean>();
    private Map<String, Long> antiSpamMap = new HashMap<String, Long>();

    private Inventory tradeMenuTemplate;
    public String menuTitle = Util.parseColor(VendEx.getPlugin().getConfig().getString("tradeConfig.tradeMenu.title"));
    public int menuSize = VendEx.getPlugin().getConfig().getInt("tradeConfig.tradeMenu.size");
    public Material moneyItem = Material.valueOf(VendEx.getPlugin().getConfig().getString("tradeConfig.tradeMenu.moneyItem"));

    private int antiSpamInterval = VendEx.getPlugin().getConfig().getInt("tradeConfig.antiSpamInterval");
    private int tradeInviteExpireInterval = VendEx.getPlugin().getConfig().getInt("tradeConfig.tradeInviteExpireInterval");

    public static TradeHandler getInstance() {
        return instance;
    }

    public void inviteToTrade(Player initiator, Player invited) {
        inviteMap.put(invited.getName(), initiator.getName());
        inviteTime.put(invited.getName(), System.currentTimeMillis());
        Messaging.sendMessage(initiator, "&aYou have invited &b" + invited.getName() + " &ato trade!");
        Messaging.sendMessage(invited, "&b" + initiator.getName() + "&a has invited you to trade! Type &b/trade accept&a to trade!");
    }

    public void tryAcceptTrade(Player invited) {
        if(!inviteMap.containsKey(invited.getName())) Messaging.sendErrorMessage(invited, "You do not have any pending trade invites!");
        else {
            if(System.currentTimeMillis() - inviteTime.get(invited.getName()) > tradeInviteExpireInterval * 1000) Messaging.sendErrorMessage(invited, "Sorry, your trade invite has expired!");
            else if(Bukkit.getServer().getPlayerExact(inviteMap.get(invited.getName())) == null) Messaging.sendErrorMessage(invited, "Sorry, the person who invited you to trade is no longer online!");
            else new Trade(Bukkit.getServer().getPlayerExact(inviteMap.get(invited.getName())), invited);
        }
    }

    public Inventory getTradeMenuTemplate() {
        if(tradeMenuTemplate != null) return tradeMenuTemplate;
        tradeMenuTemplate = Bukkit.createInventory(null, menuSize, menuTitle);
    }
}
