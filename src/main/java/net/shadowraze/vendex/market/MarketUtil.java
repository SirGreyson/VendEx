package net.shadowraze.vendex.market;

import net.shadowraze.vendex.VendEx;

public class MarketUtil {

    public static int getShopSize(String playerName) {
        for(int i = 6; i > 0; i--)
            if(VendEx.permission.playerHas((String) null, playerName, "vendex.shop." + i)) return i;
        return 1;
    }
}
