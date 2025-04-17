package kr.co.pplus.store.api.jpa.model;

/*
      -1:결제실패, 1:결제요청, 2:결제승인, 11:취소요청, 12:취소완료, 21:환불요청, 22:환불완료, 31:교환요청, 32:교환완료
     */
public enum ReserveStatus {
    BOOKING(1), BOOKING_CANCEL(2), EXPIRED(3), COMPLETE(99);


    private final int status;

    private ReserveStatus(int status)
    {
        this.status = status;
    }

    public int getStatus() {
        return this.status ;
    }
}
