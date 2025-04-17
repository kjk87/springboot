package kr.co.pplus.store.api.jpa.model;

/*
      -1:결제실패, 1:결제요청, 2:결제승인, 11:취소요청, 12:취소완료, 21:환불요청, 22:환불완료, 31:교환요청, 32:교환완료
     */
public enum PurchaseProductStatus {
    FAIL(-1),
    PAY_REQ(1), PAY(2),
    CANCEL_REQ(11), CANCEL_COMPLETE(12),
    REFUND_REQ(21), REFUND_COMPLETE(22),
    EXCHANGE_REQ(31), EXCHANGE_COMPLETE(32),
    COMPLETE(99);


    private final int status;

    private PurchaseProductStatus(int status)
    {
        this.status = status;
    }

    public int getStatus() {
        return this.status ;
    }
}
