package net.shadowraze.vendex.trade.menus;

import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.trade.Trade;
import net.shadowraze.vendex.trade.TradeHandler;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeMenu extends Menu {

    private Inventory defaultMenu;

    private static Map<String, List<Integer>> offerSlots = new HashMap<String, List<Integer>>();

    private static ItemStack borderItem = Util.metaStack(" ", new ArrayList<String>(), Material.STAINED_GLASS_PANE, DyeColor.BLACK.getData());
    private static ItemStack offerAcceptedItem = Util.metaStack("&6Offer: &aACCEPTED", "Offer accepted", Material.STAINED_GLASS_PANE, DyeColor.LIME.getData());
    private static ItemStack offerNotAcceptedItem = Util.metaStack("&6Offer: &cNOT ACCEPTED", "Click to accept the current offer", Material.STAINED_GLASS_PANE, DyeColor.RED.getData());
    private static ItemStack addMoneyItem = Util.metaStack("&6Offer Money", "Click to add money to the trade", TradeHandler.getInstance().getMoneyItem());
    private static ItemStack addGTokenItem = Util.metaStack("&6Offer Gold Tokens", "Click to add gold tokens to the trade", TradeHandler.getInstance().getGTokenItem());
    private static ItemStack leaveTradeItem = Util.metaStack("&4Leave Trade", "Click to close the trade menu without trading", Material.STATIONARY_LAVA);

    public Map<Trade, Inventory> tradeMap = new HashMap<Trade, Inventory>();

    public TradeMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
        MenuHandler.getInstance().registerMenu(ChatColor.stripColor(getTitle()), this);
    }

    @Override
    public void loadMenu() {
        defaultMenu = Bukkit.createInventory(null, getSize(), getTitle());
        for(int rowID = 0; rowID < getSize() / 9; rowID++) {
            for(int slotID = rowID * 9; slotID < (rowID + 1) * 9; slotID++) {
                if(rowID < 3) {
                    if(slotID < (rowID * 9) + 4) getPrimaryOfferSlots().add(slotID);
                    else if(rowID < 3 && slotID == (rowID * 9) + 4) defaultMenu.setItem(slotID, borderItem);
                    else if(rowID < 3 && slotID > (rowID * 9) + 4) getSecondaryOfferSlots().add(slotID);
                } else if(rowID == 3) {
                    if(slotID == (rowID * 9) + 1 || slotID == (rowID * 9) + 6) defaultMenu.setItem(slotID, getMoneyOfferedItem(null, null));
                    else if(slotID == (rowID * 9) + 2 || slotID == (rowID * 9) + 7) defaultMenu.setItem(slotID, getGTokenOfferItem(null, null));
                    else if(slotID == (rowID * 9) + 4) defaultMenu.setItem(slotID, borderItem);
                } else if(rowID == 5) {
                    if(slotID == (rowID * 9) + 2 || slotID == (rowID * 9) + 6) defaultMenu.setItem(slotID, offerNotAcceptedItem);
                    else if(slotID == (rowID * 9) + 3) defaultMenu.setItem(slotID, addMoneyItem);
                    else if(slotID == (rowID * 9) + 4) defaultMenu.setItem(slotID, leaveTradeItem);
                    else if(slotID == (rowID * 9) + 5) defaultMenu.setItem(slotID, addGTokenItem);
                } else if(rowID == 4) defaultMenu.setItem(slotID, borderItem);
            }
        }
    }

    @Override
    public void openMenu(Player player) {

    }

    public void openMenu(Trade openTrade) {
        tradeMap.put(openTrade, Bukkit.createInventory(null, getSize(), getTitle()));
        tradeMap.get(openTrade).setContents(defaultMenu.getContents());
        openTrade.getPlayer(openTrade.getRequester()).openInventory(tradeMap.get(openTrade));
        openTrade.getPlayer(openTrade.getRequested()).openInventory(tradeMap.get(openTrade));
    }

    public Inventory getMenu(Trade getTrade) {
        return tradeMap.get(getTrade);
    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player cPlayer = (Player) e.getWhoClicked();
        Trade playerTrade = TradeHandler.getInstance().getPlayerTrade(cPlayer.getName());
        if(playerTrade == null) return;
        if(e.getRawSlot() > getSize() || getOfferSlots(playerTrade.isRequester(cPlayer.getName())).contains(e.getRawSlot())) {
            if(e.getCurrentItem() != null && !TradeHandler.getInstance().getTradeBlackList().contains(e.getCurrentItem().getType()) && e.getClick() != ClickType.SHIFT_LEFT) {
                e.setCancelled(false);
                playerTrade.resetAccepted();
            }
        }
        else if(e.getRawSlot() == getConfirmationSlot(playerTrade.isRequester(cPlayer.getName()) && playerTrade.canAccept(cPlayer.getName()))) playerTrade.setAccepted(cPlayer.getName(), !playerTrade.hasAccepted(cPlayer.getName()));
        else if(e.getRawSlot() == getSize() - 6) TradeHandler.ADD_MONEY_MENU.openMenu((Player) e.getWhoClicked());
        else if(e.getRawSlot() == getSize() - 5) e.getWhoClicked().closeInventory();
        else if(e.getRawSlot() == getSize() - 4) TradeHandler.ADD_GTOKEN_MENU.openMenu((Player) e.getWhoClicked());
    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {
        if(e.getPlayer().getOpenInventory() != null)
            if(e.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase(TradeHandler.ADD_MONEY_MENU.getTitle()) ||
            e.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase(TradeHandler.ADD_GTOKEN_MENU.getTitle())) return;
        Trade playerTrade = TradeHandler.getInstance().getPlayerTrade(e.getPlayer().getName());
        if(playerTrade == null) return;
        playerTrade.cancelTrade(e.getPlayer().getName());
    }

    public List<Integer> getOfferSlots(boolean isRequester) {
        if(isRequester) return getPrimaryOfferSlots();
        return getSecondaryOfferSlots();
    }

    public List<Integer> getPrimaryOfferSlots() {
        if(!offerSlots.containsKey("PRIMARY")) offerSlots.put("PRIMARY", new ArrayList<Integer>());
        return offerSlots.get("PRIMARY");
    }

    public List<Integer> getSecondaryOfferSlots() {
        if(!offerSlots.containsKey("SECONDARY")) offerSlots.put("SECONDARY", new ArrayList<Integer>());
        return offerSlots.get("SECONDARY");
    }

    public List<ItemStack> getTradeItems(Trade playerTrade, boolean isRequester) {
        List<ItemStack> stackList = new ArrayList<ItemStack>();
        for(Integer slotID : isRequester ? getPrimaryOfferSlots() : getSecondaryOfferSlots())
            if(tradeMap.get(playerTrade).getItem(slotID) != null) stackList.add(tradeMap.get(playerTrade).getItem(slotID));
        return stackList;
    }

    public int getConfirmationSlot(boolean isRequester) {
        if(isRequester) return getSize() - 7;
        return getSize() - 3;
    }

    public void updateConfirmationItem(Trade playerTrade, String playerName) {
        tradeMap.get(playerTrade).setItem(getConfirmationSlot(playerTrade.isRequester(playerName)), playerTrade.hasAccepted(playerName) ? offerAcceptedItem : offerNotAcceptedItem);
    }

    public ItemStack getMoneyOfferedItem(Trade playerTrade, String playerName) {
        if(playerTrade == null) return Util.metaStack("&6Money: &a0", "Money offered", TradeHandler.getInstance().getMoneyItem());
        return Util.metaStack("&6Money: &a" + playerTrade.getMoneyOffered(playerName), "Money offered", TradeHandler.getInstance().getMoneyItem());
    }

    public int getMoneyOfferSlot(boolean isRequester) {
        if(isRequester) return getSize() - 26;
        return getSize() - 21;
    }

    public void updateMoneyOfferItem(Trade playerTrade, String playerName) {
        tradeMap.get(playerTrade).setItem(getMoneyOfferSlot(playerTrade.isRequester(playerName)), getMoneyOfferedItem(playerTrade, playerName));
    }

    public ItemStack getGTokenOfferItem(Trade playerTrade, String playerName) {
        if(playerTrade == null) return Util.metaStack("&6Gold Tokens: &a0", "Gold Tokens offered", TradeHandler.getInstance().getGTokenItem());
        return Util.metaStack("&6Gold Tokens: &a" + playerTrade.getGTokensOffered(playerName), "Gold Tokens offered", TradeHandler.getInstance().getGTokenItem());
    }

    public int getGTokenOfferSlot(boolean isRequester) {
        if(isRequester) return getSize() - 20;
        return getSize() - 25;
    }

    public void updateGTokenOfferItem(Trade playerTrade, String playerName) {
        tradeMap.get(playerTrade).setItem(getGTokenOfferSlot(playerTrade.isRequester(playerName)), getGTokenOfferItem(playerTrade, playerName));
    }
}
