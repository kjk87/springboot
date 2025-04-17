package kr.co.pplus.store.api.jpa.model.delivery;


public enum DeliveryCompanyType {
    PPLUS("피플러스", 1),
    BAEMIN("배달의민족", 2),
    YOGIYO("요기요", 3),
    BAEDALTONG("배달통", 4) ;

    private final String name;
    private final Integer type;

    private DeliveryCompanyType(String name, Integer process)
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
