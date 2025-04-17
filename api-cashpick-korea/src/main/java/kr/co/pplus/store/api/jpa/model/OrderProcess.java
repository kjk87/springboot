package kr.co.pplus.store.api.jpa.model;

//0: 접수대기, 1:접수완료, 2:완료, 3: 주문최소, 4:배송중(reserved)

// 0:접수대기 1:상품준비중 2:배달/배송중 3:배달/배송완료 4:반품요청 5:반품완료 6:교환요청 7:교환완료 8:구매확정
public enum OrderProcess {
    HOTDEAL_PLUS_WAIT("HOTDEAL_PLUS_WAIT", -1),
    WAIT("WAIT", 0),
    CONFIRM("CONFIRM", 1),
    DELIVERY("DELIVERY", 2),
    DELIVERY_COMPLETE("DELIVERY_COMPLETE", 3),
    REFUND_WAIT("REFUND_WAIT", 4),
    REFUND("REFUND", 5),
    CHANGE_WAIT("CHANGE_WAIT", 6),
    CHANGE("CHANGE", 7),
    COMPLETE("Complete", 8);

    private final String name;
    private final int process;

    private OrderProcess(String name, int process)
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

/*

alter table buy add column confirm_datetime datetime default null comment '주문 접수 확인' ;
alter table buy add column cancel_datetime datetime default null comment '주문 취소 확인' ;
alter table buy add column complete_datetime datetime default null comment '주문 완료 확인' ;
 */