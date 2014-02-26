package net.shadowraze.vendex.trade;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.trade.menus.AddGTokenMenu;
import net.shadowraze.vendex.trade.menus.AddMoneyMenu;
import net.shadowraze.vendex.trade.menus.TradeMenu;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeHandler implements Listener {

    private static TradeHandler instance;
    private VendEx plugin;
    private static ConfigurationSection tradeConfig = VendEx.getPlugin().getConfig().getConfigurationSection("tradeConfig");

    public static TradeMenu TRADE_MENU;
    public static AddMoneyMenu ADD_MONEY_MENU = new AddMoneyMenu(Util.parseColor(tradeConfig.getString("addMoneyMenu.title")), 9);
    public static AddGTokenMenu ADD_GTOKEN_MENU = new AddGTokenMenu(Util.parseColor(tradeConfig.getString("addGTokenMenu.title")), 9);

    private List<Material> tradeBlackList;
    private Material moneyItem;
    private Material gTokenItem;
    private int antiSpamInterval;
    private int tradeInviteExpireInterval;

    private List<Trade> tradeList;
    private List<String> tradeDisabled;
    private Map<String, String> inviteMap = new HashMap<String, String>();
    private Map<String, Long> inviteTime = new HashMap<String, Long>();
    private Map<String, Long> antiSpamMap = new HashMap<String, Long>();

    public TradeHandler(VendEx plugin) {
        this.plugin = plugin;
        this.instance = this;
        this.TRADE_MENU = new TradeMenu(plugin, Util.parseColor(tradeConfig.getString("tradeMenu.title")), 54);
    }

    public static TradeHandler getInstance() {
        return instance;
    }

    public List<Trade> getTradeList() {
        if(tradeList == null) tradeList = new ArrayList<Trade>();
        return tradeList;
    }

    public boolean canAcceptTradeInvite(Player tradePlayer) {
        if(!inviteMap.containsKey(tradePlayer.getName())) Messaging.sendErrorMessage(tradePlayer, "You have no pending trade invitations!");
        else if(getPlayerTrade(getInviter(tradePlayer.getName())) != null) {
            Messaging.sendErrorMessage(tradePlayer, "Sorry, that player is already in another trade!");
            inviteMap.remove(tradePlayer.getName());
            inviteTime.remove(tradePlayer.getName());
        } else if(Bukkit.getPlayerExact(getInviter(tradePlayer.getName())) == null) {
            Messaging.sendErrorMessage(tradePlayer, "Sorry, the player who invited you is no longer online!");
            inviteMap.remove(tradePlayer.getName());
            inviteTime.remove(tradePlayer.getName());
        } else if(System.currentTimeMillis() - inviteTime.get(tradePlayer.getName()) > (getInviteExpireInterval() * 1000)) {
            Messaging.sendErrorMessage(tradePlayer, "Sorry, that trade invitation has expired!");
            inviteMap.remove(tradePlayer.getName());
            inviteTime.remove(tradePlayer.getName());
        } else return true;
        return false;
    }

    public void createTrade(String requester, String requested) {
        getTradeList().add(new Trade(requester, requested));
        inviteMap.remove(requested);
        inviteTime.remove(requested);
    }

    public void removeTrade(Trade remTrade) {
        getTradeList().remove(remTrade);
        TRADE_MENU.tradeMap.remove(remTrade);
    }

    public Trade getPlayerTrade(String playerName) {
        for(Trade trade : getTradeList())
            if(trade.containsParticipant(playerName)) return trade;
        return null;
    }

    public boolean isTradeDisabled(String playerName) {
        if(tradeDisabled == null) tradeDisabled = VendEx.getPersistenceConfig().getStringList("tradeDisabled");
        return tradeDisabled.contains(playerName);
    }

    public void toggleTradeEnabled(String playerName) {
        if(isTradeDisabled(playerName)) tradeDisabled.remove(playerName);
        else tradeDisabled.add(playerName);
        VendEx.getPersistenceConfig().set("tradeDisabled", tradeDisabled);
        VendEx.savePersistenceConfig();
    }

    public void invitePlayer(Player inviter, Player invited) {
        inviteMap.put(invited.getName(), inviter.getName());
        inviteTime.put(invited.getName(), System.currentTimeMillis());
        antiSpamMap.put(inviter.getName(), System.currentTimeMillis());
        Messaging.sendMessage(inviter, "&aYou have sent a trade invite to &b" + invited.getName());
        Messaging.sendMessage(invited, "&b" + inviter.getName() + " &ahas invited you to trade! Type &b/trade accept&a to trade");
    }

    public String getInviter(String invited) {
        if(inviteMap.containsKey(invited)) return inviteMap.get(invited);
        return null;
    }

    public List<Material> getTradeBlackList() {
        if(tradeBlackList != null) return tradeBlackList;
        tradeBlackList = new ArrayList<Material>();
        for(String material : tradeConfig.getStringList("blackList"))
            tradeBlackList.add(Material.valueOf(material));
        return tradeBlackList;
    }

    public Material getMoneyItem() {
        if(moneyItem == null) moneyItem = Material.valueOf(tradeConfig.getString("moneyItem"));
        return moneyItem;
    }

    public Material getGTokenItem() {
        if(gTokenItem == null) gTokenItem = Material.valueOf(tradeConfig.getString("goldTokenItem"));
        return gTokenItem;
    }

    public int getAntiSpamInterval() {
        if(antiSpamInterval == 0) antiSpamInterval = tradeConfig.getInt("antiSpamInterval");
        return antiSpamInterval;
    }

    public int getInviteExpireInterval() {
        if(tradeInviteExpireInterval == 0) tradeInviteExpireInterval = tradeConfig.getInt("tradeInviteExpireInterval");
        return tradeInviteExpireInterval;
    }

    @EventHandler
    public void onPlayerInvite(PlayerInteractEntityEvent e) {
        if(!(e.getRightClicked() instanceof Player)) return;
        if(!e.getPlayer().isSneaking()) return;
        if(isTradeDisabled(((Player) e.getRightClicked()).getName())) Messaging.sendErrorMessage(e.getPlayer(), "This player is not accepting trade invites at this time!");
        else if(antiSpamMap.containsKey(e.getPlayer().getName()) && (System.currentTimeMillis() - antiSpamMap.get(e.getPlayer().getName())) / 1000 <= getAntiSpamInterval())
            Messaging.sendErrorMessage(e.getPlayer(), "You must wait before you can send another trade invite!");
        else if(inviteMap.containsKey(((Player) e.getRightClicked()).getName()) && inviteMap.get(((Player) e.getRightClicked()).getName()).equalsIgnoreCase(e.getPlayer().getName()) &&
                (System.currentTimeMillis() - inviteTime.get(((Player) e.getRightClicked()).getName())) / 1000 < getInviteExpireInterval())
            Messaging.sendErrorMessage(e.getPlayer(), "This player already has a pending trade invite from you!");
        else invitePlayer(e.getPlayer(), (Player) e.getRightClicked());
    }
}
