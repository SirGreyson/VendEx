package net.shadowraze.vendex.tradeFinal.menus;

import net.shadowraze.vendex.menu.Menu;
import net.shadowraze.vendex.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    private static Map<String, Integer> confirmationSlots = new HashMap<String, Integer>();

    private static ItemStack borderItem = Util.metaStack(" ", new ArrayList<String>(), Material.STAINED_GLASS_PANE, DyeColor.BLACK.getData());

    public TradeMenu(String menuTitle, int menuSize) {
        super(menuTitle, menuSize);
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
                }
            }
        }
    }

    @Override
    public void openMenu(Player player) {

    }

    @Override
    public void onMenuClick(InventoryClickEvent e) {

    }

    @Override
    public void onMenuClose(InventoryCloseEvent e) {

    }

    public List<Integer> getPrimaryOfferSlots() {
        if(!offerSlots.containsKey("PRIMARY")) offerSlots.put("PRIMARY", new ArrayList<Integer>());
        return offerSlots.get("PRIMARY");
    }

    public List<Integer> getSecondaryOfferSlots() {
        if(!offerSlots.containsKey("SECONDARY")) offerSlots.put("SECONDARY", new ArrayList<Integer>());
        return offerSlots.get("SECONDARY");
    }
}
