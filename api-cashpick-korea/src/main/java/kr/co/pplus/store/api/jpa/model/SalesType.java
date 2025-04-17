package kr.co.pplus.store.api.jpa.model;

// 판매분류 - 1:매장판매, 2:배달, 3:배송, 4:예약, 5:픽업, 6:티켓
public enum SalesType {
    STORE(1), DELIVERY(2),
    SHIPPING(3), RESERVE(4), PICKUP(5), TICKET(6);;


    private final long type;

    private SalesType(long type)
    {
        this.type = type;
    }

    public long getType() {
        return this.type ;
    }
}
