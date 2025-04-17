package kr.co.pplus.store.api.jpa.model;

public enum LpngProcess {
    WAIT("WAIT", 0),
    PAY("PAY", 1),
    CANCEL("CANCEl", 2);

    private final String name;
    private final Integer type;

    private LpngProcess(String name, Integer process)
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
