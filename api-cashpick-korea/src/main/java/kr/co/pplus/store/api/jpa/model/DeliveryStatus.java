package kr.co.pplus.store.api.jpa.model;

// 배송 상태값. 0:확인전 1:상품준비중, 2:주문취소, 3:배송중, 4:배송완료, 5:환불수거중, 6:환불수거완료, 7:교환수거중, 8:교환상품준비중, 9:교환배송중, 10:교환배송완료, 99:구매확정
public enum DeliveryStatus {
    BEFORE_READY(0), READY(1), CANCEL(2), ING(3), COMPLETE(4), REFUND_ING(5), REFUND_COMPLETE(6),
    EXCHANGE_ING(7), EXCHANGE_RETURN_READY(8), EXCHANGE_RETURN_ING(9), EXCHANGE_RETURN_COMPLETE(10),
    PURCHASE_COMPLETE(99);


    private final int status;

    private DeliveryStatus(int status)
    {
        this.status = status;
    }

    public int getStatus() {
        return this.status ;
    }
}
