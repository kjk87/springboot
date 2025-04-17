package kr.co.pplus.store.api.jpa.model.type;

//'0: 메뉴, 1포장주문, 2:배달주문, 3:배송(reserved)',
public enum BuyType {
    MENU_GOODS("MenuGoods", 0),
    MINI_SHOP_GOODS("MiniShopGoods", 1),
    CASH_GOODS("CashGoods", 2) ;

    private final String name;
    private final int type;

    private BuyType(String name, int process)
    {
        this.name = name;
        this.type = process;
    }

    public String getName() {
        return this.name ;
    }

    public int getType() {
        return this.type ;
    }
}
