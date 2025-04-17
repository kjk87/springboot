package kr.co.pplus.store.api.jpa.model.type;

//'0: 메뉴, 1포장주문, 2:배달주문, 3:배송(reserved)',
public enum GoodsPlusType {
    GENERAL("General", 0),
    HOTDEAL("Hotdeal", 1),
    PLUS("CashGoods", 2) ;

    private final String name;
    private final int type;

    private GoodsPlusType(String name, int process)
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
