package net.shadowraze.vendex.cmd.cmds;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.cmd.RootCommand;
import net.shadowraze.vendex.cmd.SubCommand;
import net.shadowraze.vendex.market.MarketManager;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import net.shadowraze.vendex.util.Variables;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class MarketCmd extends RootCommand {

    @Override
    public void registerSubCommands() {
        registerSubCommand("setregion", new SetRegion());
        registerSubCommand("getblacklist", new GetBlackList());
        registerSubCommand("addvendor", new AddVendor());
        registerSubCommand("removevendor", new RemoveVendor());
        registerSubCommand("listvendors", new ListVendors());
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if(VendEx.permission.playerHas(player, Variables.ADMIN_PERM)) {
            if(args.length == 0) Messaging.sendMessage(player, helpMessage());
            else if(args.length <= 2 && getSubCommands().containsKey(args[0].toLowerCase()))
                getSubCommands().get(args[0].toLowerCase()).onCommand(player, args);
            else Messaging.sendMessage(player, helpMessage());
        }
        return true;
    }

    @Override
    public String helpMessage() {
        return ChatColor.BLUE + "Available Market Options:\n" +
                "setregion - set the name of the market region\n" +
                "getblacklist- get a list of blacklisted materials\n" +
                "addvendor - add a new vendor at your location\n" +
                "removevendor <index> - remove a vendor with the given index\n" +
                "listvendors - list vendors and their respective index";
    }

    class SetRegion implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
                if(args.length != 2) Messaging.sendMessage(player, helpMessage());
                else {
                    VendEx.getPlugin().getConfig().set("worldGuard.regionName", args[1]);
                    VendEx.getPlugin().saveConfig();
                    Messaging.sendMessage(player, Variables.MESSAGES.get("wgRegionSet"));
                }
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.RED + "Syntax error! Try /market setregion <name>";
        }
    }

    class GetBlackList implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            Messaging.sendMessage(player, MarketManager.getBlackList().toString());
            return true;
        }

        @Override
        public String helpMessage() {
            return null;
        }
    }

    class AddVendor implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
                if(Util.isInRegion(player, Variables.WG_REGION)) {
                    MarketManager.getVendors().put(player.getLocation(), MarketManager.vendorVillager(player.getLocation()));
                    MarketManager.saveVendors();
                } else Messaging.sendErrorMessage(player, "Error! You cannot place a vendor outside of the market region!");
            return true;
        }

        @Override
        public String helpMessage() {
            return null;
        }
    }

    class ListVendors implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
                Messaging.sendMessage(player, ChatColor.BLUE + "Vendor Locations: ");
                int i = 0;
                for(Location loc : MarketManager.getVendors().keySet())
                    Messaging.sendMessage(player, ChatColor.BLUE + "" + i + ": " + ChatColor.AQUA + Util.parseLocation(loc));
            return true;
        }

        @Override
        public String helpMessage() {
            return null;
        }
    }

    class RemoveVendor implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if(VendEx.permission.playerHas(player, Variables.ADMIN_PERM)) {
                if(args.length != 2) Messaging.sendMessage(player, helpMessage());
                else {
                    try {
                        int index = Integer.parseInt(args[1]);
                        if(index >= MarketManager.getVendors().keySet().size())
                            Messaging.sendErrorMessage(player, "Error! Index out of range! Type " + ChatColor.AQUA + "/market listvendors" + ChatColor.RED + " for options");
                        else {
                            Iterator<Location> locIt = MarketManager.getVendors().keySet().iterator();
                            int counter = 0;
                            while(locIt.hasNext()) {
                                Location spawnLoc = locIt.next();
                                if(counter == index) {
                                    MarketManager.getVendors().get(spawnLoc).setHealth(0);
                                    locIt.remove();
                                    MarketManager.saveVendors();
                                    break;
                                }
                                counter++;
                            }
                        }
                    } catch (NumberFormatException e) {
                        Messaging.sendErrorMessage(player, args[1] + " is not a number! Type " + ChatColor.AQUA + "/market listvendors" + ChatColor.RED + " for options");
                    }
                }
            }
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.RED + "Syntax error! Try /market removevendor <index>";
        }
    }
}
