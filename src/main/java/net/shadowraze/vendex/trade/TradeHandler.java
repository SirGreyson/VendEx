package net.shadowraze.vendex.trade;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeHandler implements Listener {

    public static TradeHandler instance = new TradeHandler();
    public static ConfigurationSection tradeConfig = VendEx.getPlugin().getConfig().getConfigurationSection("tradeConfig");
    public static int antiSpamInterval = tradeConfig.getInt("antiSpamInterval");
    public static int inviteExpireInterval = tradeConfig.getInt("tradeInviteExpireInterval");
    public static List<Material> tradeBlackList;
    public static Material moneyItem = Material.valueOf(tradeConfig.getString("moneyItem"));
    public static Material gTokenItem = Material.valueOf(tradeConfig.getString("goldTokenItem"));
    public static String tradeMenuTitle = Util.parseColor(tradeConfig.getString("tradeMenu.title"));
    public static int tradeMenuSize = tradeConfig.getInt("tradeMenu.size");

    public static Map<String, Trade> tradeMap = new HashMap<String, Trade>();
    public static Inventory defaultTradeMenu = getDefaultTradeMenu();

    public static TradeHandler getInstance() {
        return instance;
    }

    private static Inventory getDefaultTradeMenu() {
        Inventory tradeInventory = Bukkit.createInventory(null, tradeMenuSize, tradeMenuTitle);
        for (int i = 0; i < tradeInventory.getSize() / 9; i++) {
            int slotCounter = 1;
            for (int j = i * 9; j < (i + 1) * 9; j++) {
                if (i == (tradeInventory.getSize() / 9) - 3) {
                    if(slotCounter == 2 || slotCounter == 7)
                        tradeInventory.setItem(j, Util.metaStack("&6Money: &a0", new ArrayList<String>() {{
                            add("Money offered");
                        }}, TradeHandler.moneyItem));
                    else if(slotCounter == 3 || slotCounter == 8)
                        tradeInventory.setItem(j, Util.metaStack("&6Gold Tokens: &a0", new ArrayList<String>() {{
                            add("Gold Tokens offered");
                        }}, TradeHandler.gTokenItem));
                    else if(slotCounter == 5)
                        tradeInventory.setItem(j, Util.metaStack(" ", new ArrayList<String>(), Material.STAINED_GLASS_PANE, DyeColor.BLACK.getData()));
                    slotCounter++;
                }
                else if (i == (tradeInventory.getSize() / 9) - 2)
                    tradeInventory.setItem(j, Util.metaStack(" ", new ArrayList<String>(), Material.STAINED_GLASS_PANE, DyeColor.BLACK.getData()));
                else if (i == (tradeInventory.getSize() / 9) - 1) {
                    if (slotCounter == 3 || slotCounter == 7) {
                        tradeInventory.setItem(j, Util.metaStack("&6Offer: &cNOT ACCEPTED", new ArrayList<String>() {{
                            add("Click to accept the current offer");
                        }}, Material.STAINED_GLASS_PANE, DyeColor.RED.getData()));
                    } else if (slotCounter == 4)
                        tradeInventory.setItem(j, Util.metaStack("&6Offer Money", new ArrayList<String>() {{
                            add("Click to add money to the trade");
                        }}, TradeHandler.moneyItem));
                    else if (slotCounter == 5)
                        tradeInventory.setItem(j, Util.metaStack("&4Leave Trade", new ArrayList<String>() {{
                            add("Click to close the trade menu without trading");
                        }}, Material.STATIONARY_LAVA));
                    else if (slotCounter == 6)
                        tradeInventory.setItem(j, Util.metaStack("&6Offer Gold Tokens", new ArrayList<String>() {{
                            add("Click to add golden tokens to the trade");
                        }}, TradeHandler.gTokenItem));
                    slotCounter++;
                } else {
                    if(slotCounter == 5)
                        tradeInventory.setItem(j, Util.metaStack(" ", new ArrayList<String>(), Material.STAINED_GLASS_PANE, DyeColor.BLACK.getData()));
                        slotCounter++;
                }
            }
        }
        return tradeInventory;
    }

    public static void startTrade(String initiator, String invited) {
        Trade newTrade = new Trade(initiator, invited);
        tradeMap.put(initiator, newTrade);
        tradeMap.put(invited, newTrade);
        newTrade.openTradeInventory();
    }

    public static List<Material> getTradeBlackList() {
        if(tradeBlackList != null) return tradeBlackList;
        tradeBlackList = new ArrayList<Material>();
        for(String material : tradeConfig.getStringList("blackList"))
            tradeBlackList.add(Material.valueOf(material));
        return tradeBlackList;
    }

    @EventHandler
    public void onInvite(PlayerInteractEntityEvent e) {
        if(!(e.getRightClicked() instanceof Player)) return;
        if(!e.getPlayer().isSneaking()) return;
        Player clickedPlayer = (Player) e.getRightClicked();
        if(tradeMap.containsKey(clickedPlayer.getName())) {
            Messaging.sendErrorMessage(e.getPlayer(), "This player is already trading with someone!");
            return;
        }
        startTrade(e.getPlayer().getName(), clickedPlayer.getName());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!tradeMap.containsKey(e.getWhoClicked().getName())) return;
        if(!ChatColor.stripColor(e.getInventory().getTitle()).equalsIgnoreCase(Util.stripColor(tradeMenuTitle))) return;
        tradeMap.get(e.getWhoClicked().getName()).onClick(e);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(!tradeMap.containsKey(e.getPlayer().getName())) return;
        if(!ChatColor.stripColor(e.getInventory().getTitle()).equalsIgnoreCase(Util.stripColor(tradeMenuTitle))) return;
        tradeMap.get(e.getPlayer().getName()).onClose(e);
        tradeMap.remove(e.getPlayer().getName());
    }
}
