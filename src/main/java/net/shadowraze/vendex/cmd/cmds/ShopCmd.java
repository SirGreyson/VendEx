package net.shadowraze.vendex.cmd.cmds;

import net.shadowraze.vendex.VendEx;
import net.shadowraze.vendex.cmd.RootCommand;
import net.shadowraze.vendex.cmd.SubCommand;
import net.shadowraze.vendex.market.MarketManager;
import net.shadowraze.vendex.market.Shop;
import net.shadowraze.vendex.menu.MenuHandler;
import net.shadowraze.vendex.util.Messaging;
import net.shadowraze.vendex.util.Util;
import net.shadowraze.vendex.util.Variables;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ShopCmd extends RootCommand {

    @Override
    public void registerSubCommands() {
        registerSubCommand("create", new Create());
        registerSubCommand("delete", new Delete());
        registerSubCommand("manage", new Manage());
        registerSubCommand("open", new Open());
        registerSubCommand("view", getSubCommands().get("open"));
        registerSubCommand("addoffer", new AddOffer());
        registerSubCommand("removeoffer", new RemoveOffer());
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if(args.length == 0) Messaging.sendMessage(player, VendEx.permission.playerHas(player, Variables.ADMIN_PERM) ? adminHelpMessage() : helpMessage());
        else if(args.length > 0 && getSubCommands().containsKey(args[0].toLowerCase())) {
            if(!Variables.WG_ENABLED || VendEx.permission.playerHas(player, Variables.ADMIN_PERM)) getSubCommands().get(args[0].toLowerCase()).onCommand(player, args);
            else if(Variables.WG_ENABLED && Util.isInRegion(player, Variables.WG_REGION)) getSubCommands().get(args[0].toLowerCase()).onCommand(player, args);
            else Messaging.sendErrorMessage(player, Variables.REGION_ERROR_MSG);
        } else Messaging.sendMessage(player, VendEx.permission.playerHas(player, Variables.ADMIN_PERM) ? adminHelpMessage() : helpMessage());
        return true;
    }

    @Override
    public String helpMessage() {
        return ChatColor.BLUE + "Shop Management Options:\n" +
                "create - open your own shop\n" +
                "delete - delete your existing shop\n" +
                "manage - open the shop mangement menu\n" +
                "open - view your shop\n" +
                "addoffer - add an offer to your shop\n" +
                "removeoffer - remove an offer from your shop";
    }

    public String adminHelpMessage() {
        return ChatColor.BLUE + "Shop Management Options:\n" +
                "create - open your own shop\n" +
                "*delete - delete your existing shop\n" +
                "manage - open the shop mangement menu\n" +
                "*open - view your shop\n" +
                "*addoffer - add an offer to your shop\n" +
                "*removeoffer - remove an offer from your shop\n" +
                "\n" +
                "* - Type /shop [arg] ? to view admin commands";
    }

    class Create implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if(MarketManager.getPlayerShop(player.getName()) == null) {
                MarketManager.addShop(player.getName());
                Messaging.sendMessage(player, Variables.MESSAGES.get("shopCreated"));
                MenuHandler.MANAGE_SHOP_MENU.openMenu(player);
            } else Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("shopAlreadyExists"));
            return true;
        }

        @Override
        public String helpMessage() {
            return null;
        }
    }

    class Delete implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if (args.length == 1) {
                Shop pShop = MarketManager.getPlayerShop(player.getName());
                if (pShop != null) {
                    if (MarketManager.canRemoveShop(pShop)) {
                        MarketManager.removeShop(pShop);
                        Messaging.sendMessage(player, Variables.MESSAGES.get("shopDeleted"));
                        if(player.getOpenInventory() != null) player.closeInventory();
                    } else Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("shopCantDelete"));
                } else Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("noShopFound"));

            } else if (args.length <= 3 && VendEx.permission.playerHas(player, Variables.ADMIN_PERM)) {
                if(args.length == 2) {
                    if(args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("?")) {
                        Messaging.sendMessage(player, adminHelpMessage());
                        return true;
                    } else if(args[1].equalsIgnoreCase("servershop")) {
                        Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("cantDeleteServerShop"));
                        return true;
                    }
                    Shop pShop = MarketManager.getPlayerShop(args[1]);
                    if(pShop != null) {
                        if(MarketManager.canRemoveShop(pShop)) {
                            MarketManager.removeShop(pShop);
                            Messaging.sendMessage(player, args[1] + "'s Shop successfully deleted!");
                            if(player.getOpenInventory() != null) player.closeInventory();
                        } else Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("shopCantDelete"));
                    } else Messaging.sendErrorMessage(player, "Error! " + args[1] + " does not have a shop!");
                } else if(args.length == 3 && args[2].equalsIgnoreCase("force")) {
                    Shop pShop = MarketManager.getPlayerShop(args[1]);
                    if(pShop != null) {
                        MarketManager.removeShop(pShop);
                        Messaging.sendErrorMessage(player, args[1] + "'s Shop successfully deleted!");
                        if(player.getOpenInventory() != null) player.closeInventory();
                    } else Messaging.sendErrorMessage(player, "Error! " + args[1] + " does not have a shop!");
                }
            } else Messaging.sendMessage(player, VendEx.permission.playerHas(player, Variables.ADMIN_PERM) ? adminHelpMessage() : helpMessage());
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.RED + "Syntax error! Try /shop delete";
        }

        public String adminHelpMessage() {
            return ChatColor.BLUE + "Shop Deletion Options:\n" +
                    "delete - delete your own shop\n" +
                    "delete <playername> - try to delete a player's shop\n" +
                    "delete <playername> force - force delete a player's shop";
        }
    }

    class Manage implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if(args.length == 2 && args[1].equalsIgnoreCase("servershop")) MenuHandler.MANAGE_SERVER_SHOP_MENU.openMenu(player);
            else if (args.length == 1) {
                Shop pShop = MarketManager.getPlayerShop(player.getName());
                if(pShop == null) MenuHandler.CREATE_SHOP_MENU.openMenu(player);
                else MenuHandler.MANAGE_SHOP_MENU.openMenu(player);
            }
            return true;
        }

        @Override
        public String helpMessage() {
            return null;
        }
    }

    class Open implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("?")) {
                    Messaging.sendMessage(player, helpMessage());
                    return true;
                } else {
                    Shop pShop = MarketManager.getPlayerShop(args[1]);
                    if (pShop != null) MenuHandler.SHOP_MENU.openMenu(player, pShop);
                    else Messaging.sendErrorMessage(player, args[1] + " does not have a shop!");
                }
            } else if (args.length == 1) {
                Shop pShop = MarketManager.getPlayerShop(player.getName());
                if (pShop != null) MenuHandler.SHOP_MENU.openMenu(player, pShop);
                else Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("noShopFound"));
            } else Messaging.sendMessage(player, helpMessage());
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.BLUE + "Shop Viewing Options:\n" +
                    "open - view your own shop\n" +
                    "open <playername> - view a player's shop";
        }
    }

    class AddOffer implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if (args.length == 1) {
                Shop pShop = MarketManager.getPlayerShop(player.getName());
                if (pShop != null) MenuHandler.ADD_OFFER_MENU.openMenu(player, pShop);
                else Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("noShopFound"));
            } else if (args.length == 2) {
                if (args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("?"))
                    Messaging.sendMessage(player, (VendEx.permission.playerHas(player, Variables.ADMIN_PERM) ? adminHelpMessage() : helpMessage()));
                else if (!VendEx.permission.playerHas(player, Variables.ADMIN_PERM))
                    Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("editOffersNoPerms"));
                else {
                    Shop pShop = MarketManager.getPlayerShop(args[1]);
                    if (pShop != null) MenuHandler.ADD_OFFER_MENU.openMenu(player, pShop);
                    else Messaging.sendErrorMessage(player, args[1] + " does not have a shop!");
                }
            } else Messaging.sendMessage(player, helpMessage());
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.BLUE + "Add Offer Options:\n" +
                    "addoffer - add an offer to your own shop";
        }

        public String adminHelpMessage() {
            return ChatColor.BLUE + "Add Offer Options:\n" +
                    "addoffer - add an offer to your own shop\n" +
                    "addoffer <playername> - add an offer to a player's shop";
        }
    }

    class RemoveOffer implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if(args.length <= 2) {
                if(args.length == 2 && !VendEx.permission.playerHas(player, Variables.ADMIN_PERM)) {
                    Messaging.sendErrorMessage(player, Variables.ERRMESSAGES.get("editOffersNoPerms"));
                    return true;
                } else if (args.length == 2)
                    if(args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("?")) {
                        Messaging.sendMessage(player, adminHelpMessage());
                        return true;
                    }
                Shop pShop = MarketManager.getPlayerShop(args.length < 2 ? player.getName() : args[1]);
                if (pShop != null) MenuHandler.REMOVE_OFFER_MENU.openMenu(player, pShop);
                else Messaging.sendErrorMessage(player, args.length < 2 ? Variables.ERRMESSAGES.get("noShopFound") : args[1] + " does not have a shop!");
            } else Messaging.sendMessage(player, args.length == 2 ? adminHelpMessage() : helpMessage());
            return true;
        }

        @Override
        public String helpMessage() {
            return ChatColor.BLUE + "Remove Offer Options:\n" +
                    "removeoffer - remove an offer from your own shop";
        }

        public String adminHelpMessage() {
            return ChatColor.BLUE + "Remove Offer Options:\n" +
                    "removeoffer - remove an offer from your own shop\n" +
                    "removeoffer <playername> - remove an offer from a player's shop";
        }
    }
}
