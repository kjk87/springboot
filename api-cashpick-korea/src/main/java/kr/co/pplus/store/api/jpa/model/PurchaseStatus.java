package kr.co.pplus.store.api.jpa.model;

/*
      1:결제요청, 2:결제승인
      11:취소요청, 12:부분취소요청, 13:취소완료, 14:부분취소완료
      21:환불요청, 22:부분환불요청, 23:환불완료, 24:부분환불완료
      31:교환요청, 32:부분교환요청, 33:교환완료, 34:부분교환완료
      99:완료처리
     */
public enum PurchaseStatus {
    FAIL(-1),
    PAY_REQ(1), PAY(2),
    CANCEL_REQ(11), PART_CANCEL_REQ(12), CANCEL_COMPLETE(13), PART_CANCEL_COMPLETE(14),
    REFUND_REQ(21), PART_REFUND_REQ(22), REFUND_COMPLETE(23), PART_REFUND_COMPLETE(24),
    EXCHANGE_REQ(31), PART_EXCHANGE_REQ(32), EXCHANGE_COMPLETE(33), PART_EXCHANGE_COMPLETE(34),
    COMPLETE(99);


    private final int status;

    private PurchaseStatus(int status)
    {
        this.status = status;
    }

    public int getStatus() {
        return this.status ;
    }
}
