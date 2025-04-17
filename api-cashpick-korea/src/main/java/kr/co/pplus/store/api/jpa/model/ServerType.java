package kr.co.pplus.store.api.jpa.model;

//'0: 매장주문, 1포장주문, 2:배달주문, 3:배송(reserved)',
public enum ServerType {
    LOCAL("LOCAL", 1),
    DEV("DEV", 2),
    STAGE("STAGE", 3),
    PROD("PROD", 4);

    private final String name;
    private final Integer type;

    private ServerType(String name, Integer process)
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
