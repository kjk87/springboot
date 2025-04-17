package kr.co.pplus.store.api.jpa.model;

// 0:결제요청, 1:후불주문요청(접수대기), 2:결제승인(접수대기), 3:접수완료, 4:취소요청, 5:취소완료, 99:완료처리

public enum OrderPurchaseStatus {
    FAIL(-1),
    PAY_REQ(0),AFTER_PAY(1), PAY(2), CONFIRM(3), CANCEL_REQ(4), CANCEL_COMPLETE(5), COMPLETE(99);


    private final int status;

    private OrderPurchaseStatus(int status)
    {
        this.status = status;
    }

    public int getStatus() {
        return this.status ;
    }
}
