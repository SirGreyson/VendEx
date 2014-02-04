package net.shadowraze.vendex.tradeFinal;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.tradeFinal.menus.TradeMenu;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class TradeHandler {

    private static TradeHandler instance = new TradeHandler();
    private static ConfigurationSection tradeConfig = VendEx.getPlugin().getConfig().getConfigurationSection("tradeConfig");

    public static TradeMenu TRADE_MENU = new TradeMenu(tradeConfig.getString("tradeMenu.title"), 54);

    private String tradeMenuTitle;
    private List<Material> tradeBlackList;
    private Material moneyItem;
    private Material gTokenItem;

    private List<Trade> tradeList;

    public TradeHandler getInstance() {
        return instance;
    }

    public List<Trade> getTradeList() {
        if(tradeList == null) tradeList = new ArrayList<Trade>();
        return tradeList;
    }

    public void createTrade(String requester, String requested) {
        tradeList.add(new Trade(requester, requested));
    }

    public void removeTrade(Trade remTrade) {
        tradeList.remove(remTrade);
    }

    public Trade getPlayerTrade(String playerName) {
        for(Trade trade : tradeList)
            if(trade.containsParticipant(playerName)) return trade;
        return null;
    }

    public String getTradeMenuTitle() {
        if(tradeMenuTitle == null) tradeMenuTitle = Util.parseColor(tradeConfig.getString("tradeMenu.title"));
        return tradeMenuTitle;
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
}
