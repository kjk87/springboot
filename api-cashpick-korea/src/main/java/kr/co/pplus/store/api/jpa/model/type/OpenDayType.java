package kr.co.pplus.store.api.jpa.model.type;

//0:주중, 1:주말, 2:요일별
public enum OpenDayType {
    WEEK_DAY("WeekDay", 0),
    WEEK_END("WeekEnd", 1),
    EACH_DAY("EachDay", 2) ;

    private final String name;
    private final int type;

    private OpenDayType(String name, int process)
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
