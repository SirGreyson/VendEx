package net.shadowraze.vendex;

import net.shadowraze.vendex.market.MarketManager;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Util;
import net.shadowraze.vendex.util.Variables;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private VendEx plugin;
    public PlayerListener(VendEx plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(VendEx.getPersistenceConfig().getConfigurationSection("savedTradeItems." + e.getPlayer().getName()) == null) return;
        ConfigurationSection iSec = VendEx.getPersistenceConfig().getConfigurationSection("savedTradeItems." + e.getPlayer().getName() + ".tradeItems");
        for(int i = 0; i < 12; i++) {
            if(iSec.getItemStack(String.valueOf(i)) == null) break;
            e.getPlayer().getInventory().addItem(iSec.getItemStack(String.valueOf(i)));
        }
        VendEx.getPersistenceConfig().set("savedTradeItems." + e.getPlayer().getName(), null);
    }

    @EventHandler
    public void onVendorClick(PlayerInteractEntityEvent e) {
        if(!(e.getRightClicked() instanceof Villager)) return;
        if(!Util.isInRegion(e.getRightClicked(), Variables.WG_REGION)) return;
        e.setCancelled(true);
        if(ChatColor.stripColor(((Villager) e.getRightClicked()).getCustomName()).equalsIgnoreCase(Util.stripColor(MarketManager.vendorName)))
            MenuHandler.VENDEX_MENU.openMenu(e.getPlayer());
    }

    @EventHandler
    public void onVendorDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Villager)) return;
        if(!Util.isInRegion(e.getEntity(), Variables.WG_REGION)) return;
        e.setCancelled(true);
    }
}
