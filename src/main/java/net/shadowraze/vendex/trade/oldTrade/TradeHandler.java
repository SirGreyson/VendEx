package net.shadowraze.vendex.trade.oldTrade;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeHandler implements Listener {

    private static TradeHandler instance = new TradeHandler();
    public Map<String, Long> antiSpamMap = new HashMap<String, Long>();
    public Map<String, Boolean> acceptingInvites = new HashMap<String, Boolean>();
    public Map<String, String> inviteMap = new HashMap<String, String>();
    public Map<String, Long> invitedTime = new HashMap<String, Long>();
    private List<Trade> tradeList = new ArrayList<Trade>();

    private int antiSpamInterval = VendEx.getPlugin().getConfig().getInt("tradeConfig.antiSpamInterval");
    private int tradeInviteExpireInterval = VendEx.getPlugin().getConfig().getInt("tradeConfig.tradeInviteExpireInterval");

    public static TradeHandler getInstance() {
        return instance;
    }

    public Trade getTrade(Player player) {
        for(Trade trade : tradeList)
            if(trade.getPlayerOne() == player || trade.getPlayerTwo() == player) return trade;
        return null;
    }

    public void invitePlayerToTrade(Player requester, Player requested) {
        antiSpamMap.put(requester.getName(), System.currentTimeMillis());
        inviteMap.put(requested.getName(), requester.getName());
        invitedTime.put(requested.getName(), System.currentTimeMillis());
        Messaging.sendMessage(requester, "You have invited " + requested.getName() + " to trade!");
        Messaging.sendMessage(requested, requester.getName() + " has invited you to trade! Type /trade accept to trade!");
    }

    public void tryAcceptTrade(Player accepting) {
        if(!inviteMap.containsKey(accepting.getName())) Messaging.sendErrorMessage(accepting, "Error! You do not have any pending trade invites!");
        else if (System.currentTimeMillis() - invitedTime.get(accepting.getName()) > tradeInviteExpireInterval * 1000) Messaging.sendErrorMessage(accepting, "Sorry, your trade invitation has expired!");
        else if (Bukkit.getServer().getPlayerExact(inviteMap.get(accepting.getName())) == null) Messaging.sendErrorMessage(accepting, "Sorry, the person who invited you is no longer online!");
        else tradeList.add(new Trade(Bukkit.getServer().getPlayerExact(inviteMap.get(accepting.getName())), accepting));
    }

    public void toggleTrade(Player player) {
        if(!acceptingInvites.containsKey(player.getName())) acceptingInvites.put(player.getName(), false);
        else acceptingInvites.put(player.getName(), !acceptingInvites.get(player.getName()));
        Messaging.sendMessage(player, ChatColor.RED + "Trade Invites: " + ChatColor.AQUA + (acceptingInvites.get(player.getName()) ? "Enabled" : "Disabled"));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        if(!(e.getRightClicked() instanceof Player)) return;
        if(!e.getPlayer().isSneaking()) return;
        Player cPlayer = (Player) e.getRightClicked();
        if(acceptingInvites.containsKey(cPlayer.getName()) && !acceptingInvites.get(cPlayer.getName()))
            Messaging.sendErrorMessage(e.getPlayer(), "Sorry! " + cPlayer.getName() + " is not currently excepting trade invites!");
        else if(antiSpamMap.containsKey(e.getPlayer().getName()) && System.currentTimeMillis() - antiSpamMap.get(e.getPlayer().getName()) < antiSpamInterval * 1000)
            Messaging.sendErrorMessage(e.getPlayer(), "Please do not spam trade invites!");
        else if(inviteMap.containsKey(e.getPlayer().getName()) && inviteMap.get(e.getPlayer().getName()).equalsIgnoreCase(cPlayer.getName()))
            Messaging.sendErrorMessage(e.getPlayer(), "This player already invited you to trade! Type /trade accept to trade!");
        else if(inviteMap.containsKey(cPlayer.getName()) && inviteMap.get(cPlayer.getName()).equalsIgnoreCase(e.getPlayer().getName()))
            Messaging.sendErrorMessage(e.getPlayer(), "You have already have an trade invitation pending with this player!");
        else invitePlayerToTrade(e.getPlayer(), cPlayer);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!ChatColor.stripColor(e.getInventory().getTitle()).equalsIgnoreCase(Util.stripColor(Trade.menuTitle))) return;
        e.setCancelled(true);
        if(getTrade((Player) e.getWhoClicked()) != null) {

        }
    }
}
