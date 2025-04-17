package kr.co.pplus.store.api.jpa.model;

//'0: 매장주문, 1포장주문, 2:배달주문, 3:배송(reserved)',
public enum GoodsType {
    MENU_GOODS("MenuGoods", 0),
    MINI_SHOP_GOODS("MiniShopGoods", 1),
    COUPON_GOODS("CouponGoods", 2),
    CASH_GOODS("CashGoods", 3) ;

    private final String name;
    private final Integer type;

    private GoodsType(String name, Integer process)
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
