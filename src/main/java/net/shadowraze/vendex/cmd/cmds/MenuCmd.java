package net.shadowraze.vendex.cmd.cmds;

import net.shadowraze.vendex.cmd.RootCommand;
import net.shadowraze.vendex.cmd.SubCommand;
import net.shadowraze.vendex.market.MarketManager;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import net.shadowraze.vendex.util.Variables;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MenuCmd extends RootCommand {

    private net.shadowraze.vendex.VendEx vendEx;

    @Override
    public void registerSubCommands() {
        registerSubCommand("vendex", new VendEx());
        registerSubCommand("main", getSubCommands().get("vendex"));
        registerSubCommand("admin", new Admin());
        registerSubCommand("market", new Market());
        registerSubCommand("servershop", new ServerShop());
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if(args.length == 0)
            Messaging.sendMessage(player, vendEx.permission.playerHas(player, Variables.ADMIN_PERM) ? adminHelpMessage() : helpMessage());
        else if(args.length <= 2 && getSubCommands().containsKey(args[0].toLowerCase())) {
            if(!Variables.WG_ENABLED || vendEx.permission.playerHas(player, Variables.ADMIN_PERM)) getSubCommands().get(args[0].toLowerCase()).onCommand(player, args);
            else if(Variables.WG_ENABLED && Util.isInRegion(player, Variables.WG_REGION)) getSubCommands().get(args[0].toLowerCase()).onCommand(player, args);
            else Messaging.sendErrorMessage(player, Variables.REGION_ERROR_MSG);
        } else Messaging.sendMessage(player, vendEx.permission.playerHas(player, Variables.ADMIN_PERM) ? adminHelpMessage() : helpMessage());
        return true;
    }

    @Override
    public String helpMessage() {
        return ChatColor.BLUE + "Available Menu Options:\n" +
                "vendex - main plugin menu\n" +
                "market - player market menu\n" +
                "servershop - server shop menu";
    }

    public String adminHelpMessage() {
        return helpMessage().concat("\nadmin - administrative menu");
    }

    //Open VendEx Menu
    class VendEx implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if(vendEx.permission.playerHas(player, Variables.ADMIN_PERM)) MenuHandler.ADMIN_MENU.openMenu(player);
            else MenuHandler.VENDEX_MENU.openMenu(player);
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.RED + "Syntax error! Try /menu vendex";
        }
    }

    //Open Administrative Menu
    class Admin implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if(vendEx.permission.playerHas(player, Variables.ADMIN_PERM)) {
                if(args.length == 1) MenuHandler.ADMIN_MENU.openMenu(player);
                else if(args.length == 2 && args[1].equalsIgnoreCase("toggle")) MenuHandler.ADMIN_MENU.toggleMenu();
                else Messaging.sendMessage(player, helpMessage());
            } else Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("noMenuPermission"));
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.BLUE + "Available Menu Options:\n" +
                    "toggle - toggle between admin and normal view";
        }
    }

    //Open Server Shop Menu
    class ServerShop implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            MenuHandler.SHOP_MENU.openMenu(player, MarketManager.serverShop);
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.RED + "Syntax error! Try /menu servershop";
        }
    }

    //Open Market Menu
    class Market implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            MenuHandler.MARKET_MENU.openMenu(player, 0);
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.RED + "Syntax error! Try /menu market";
        }
    }
}
