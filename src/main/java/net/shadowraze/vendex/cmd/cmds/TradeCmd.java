package net.shadowraze.vendex.cmd.cmds;

import net.shadowraze.vendex.cmd.RootCommand;
import net.shadowraze.vendex.cmd.SubCommand;
import net.shadowraze.vendex.trade.oldTrade.TradeHandler;
import net.shadowraze.vendex.util.Messaging;
import org.bukkit.entity.Player;

public class TradeCmd extends RootCommand {

    @Override
    public void registerSubCommands() {
        registerSubCommand("toggle", new Toggle());
        registerSubCommand("accept", new Accept());
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if(getSubCommands().containsKey(args[0].toLowerCase())) getSubCommands().get(args[0].toLowerCase()).onCommand(player, args);
        else Messaging.sendMessage(player, helpMessage());
        return true;
    }

    @Override
    public String helpMessage() {
        return null;
    }

    class Toggle implements SubCommand {

        @Override
        public boolean onCommand(Player player, String[] args) {
            TradeHandler.getInstance().toggleTrade(player);
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
            TradeHandler.getInstance().tryAcceptTrade(player);
            return true;
        }

        @Override
        public String helpMessage() {
            return null;
        }
    }
}
