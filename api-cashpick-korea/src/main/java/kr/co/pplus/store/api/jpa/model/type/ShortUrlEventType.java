package kr.co.pplus.store.api.jpa.model.type;

public enum ShortUrlEventType {
    ETC("etc", 0),
    CLICK("click", 1),
    BUY("buy", 2) ;

    private final String name;
    private final int type;

    private ShortUrlEventType(String name, int process)
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
