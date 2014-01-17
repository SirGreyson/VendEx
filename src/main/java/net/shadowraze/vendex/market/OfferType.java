package net.shadowraze.vendex.market;

public enum OfferType {

    BUY,
    SELL;

    public static String getString(OfferType offerType) {
        if(offerType == BUY) return "Buy";
        else return "Sell";
    }
}
