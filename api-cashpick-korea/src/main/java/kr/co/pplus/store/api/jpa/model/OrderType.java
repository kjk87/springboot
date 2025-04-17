package kr.co.pplus.store.api.jpa.model;

//'0: 매장주문, 1포장주문, 2:배달주문, 3:배송',4:예약
public enum OrderType {
    SHOP_ORDER("ShopOrder", 0),
    WRAP_ORDER("WrapOrder", 1),
    DELIVERY_ORDER("DeliveryOrder", 2),
    SHIPPING_ORDER("ShippingOrder", 3),
    RESERVED("reserved", 4),
    PICK_UP("pickUp", 5),
    TICKET("ticket", 6);

    private final String name;
    private final int type;

    private OrderType(String name, int process)
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
