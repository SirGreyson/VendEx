package net.shadowraze.vendex.cmd.cmds;

import net.shadowraze.vendex.cmd.RootCommand;
import net.shadowraze.vendex.cmd.SubCommand;
import net.shadowraze.vendex.trade.TradeHandler;
import net.shadowraze.vendex.util.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TradeCmd extends RootCommand {

    @Override
    public void registerSubCommands() {
        registerSubCommand("toggle", new Toggle());
        registerSubCommand("accept", new Accept());
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if(args.length == 0) Messaging.sendMessage(player, helpMessage());
        else if(getSubCommands().containsKey(args[0].toLowerCase())) getSubCommands().get(args[0].toLowerCase()).onCommand(player, args);
        else Messaging.sendMessage(player, helpMessage());
        return true;
    }

    @Override
    public String helpMessage() {
        return ChatColor.BLUE + "Trade Management Options:\n" +
                "toggle - toggle trade invitation status\n" +
                "accept - accept current trade invitation";
    }

    class Toggle implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            TradeHandler.getInstance().toggleTradeEnabled(player.getName());
            Messaging.sendMessage(player, "&bTrade Invites: " + (TradeHandler.getInstance().isTradeDisabled(player.getName()) ? "&cDENY" : "&aALLOW"));
            return true;
        }

        @Override
        public String helpMessage() {
            return null;
        }
    }

    class Accept implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            if(TradeHandler.getInstance().canAcceptTradeInvite(player)) TradeHandler.getInstance().createTrade(TradeHandler.getInstance().getInviter(player.getName()), player.getName());
            return true;
        }

        @Override
        public String helpMessage() {
            return null;
        }
    }
}
