package kr.co.pplus.store.type.model.code;

// 판매분류 - 매장판매1, 배달2, 배송3, 예약4, 픽업5
public enum SalesType {
    STORE(1L),//배달
    DELIVERY(2L),
    SHIPPING(3L),
    RESERVATION(4L),
    PICKUP(5L);

    private final Long type;

    private SalesType(Long type)
    {
        this.type = type;
    }

    public Long getType() {
        return this.type ;
    }
}