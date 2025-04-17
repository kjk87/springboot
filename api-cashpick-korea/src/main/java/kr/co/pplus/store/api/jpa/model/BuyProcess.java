package kr.co.pplus.store.api.jpa.model;

import lombok.Data;


public enum BuyProcess {
    ERROR("Error", -2), //서버 로직 에러 : 이 경우 결제 여부 관리자가 파악해야 함.
    DENIED("Denied", -1), //결제 승인 안되는 경우 오류. PG사 승인 거부
    WAIT("Wait", 0), //reserved
    PAY("Pay", 1),
    USER_CANCEL("UserCancel", 2),
    USE("Use", 3),
    REFUND("Refund", 4),
    EXPIRED("Expired", 5) ,
    USER_CANCEL_WAIT("UserCancelWait", 6),
    USE_WAIT("UseWait", 7),
    REFUND_WAIT("RefundWait", 8),
    EXPIRE_WAIT("ExpireWait", 9),
    BIZ_CANCEL("BizCancel", 10),
    BIZ_CANCEL_WAIT("BizCancelWait", 11) ;


    private final String name;
    private final int process;

    private BuyProcess(String name, int process)
    {
        this.name = name;
        this.process = process;
    }

    public String getName() {
        return this.name ;
    }

    public int getProcess() {
        return this.process ;
    }
}
