package kr.co.pplus.store.api.jpa.model;

// 배달 0:접수대기, 1:접수완료, 11:배달사 미요청  2:배달취소, 3:기사배정,  4:배달중(기사픽업), 99:배달완료

public enum RiderStatus {
    FAIL(-1),
    WAIT(0), CONFIRM(1), CANCEL(2), NOT_CALL(11), RIDER_PICK(3), DELIVERY_ING(4), COMPLETE(99);


    private final int status;

    private RiderStatus(int status)
    {
        this.status = status;
    }

    public int getStatus() {
        return this.status ;
    }
}
