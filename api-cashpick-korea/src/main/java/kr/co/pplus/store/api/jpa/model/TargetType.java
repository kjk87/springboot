package kr.co.pplus.store.api.jpa.model;

//'0: 매장주문, 1포장주문, 2:배달주문, 3:배송(reserved)',
public enum TargetType {
    GOODS("goods", 1),
    PAGE("page", 2),
    EVENT("event", 3) ;

    private final String name;
    private final Integer type;

    private TargetType(String name, Integer process)
    {
        this.name = name;
        this.type = process;
    }

    public String getName() {
        return this.name ;
    }

    public Integer getType() {
        return this.type ;
    }
}
