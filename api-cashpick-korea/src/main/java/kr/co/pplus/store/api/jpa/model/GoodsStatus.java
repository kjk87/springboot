package kr.co.pplus.store.api.jpa.model;


public enum GoodsStatus {
    DELETE("Delete", -999), //상품 삭제시...
    STOP("Stop", -2), //판매 중지 또는 대기
    EXPIRE("Expire", -1), //판매 중지 또는 대기
    SOLD_OUT("SoldOut", 0), //완판
    SELL("Sell", 1);  // 판매중


    private final String name;
    private final Integer status;

    private GoodsStatus(String name, int status)
    {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return this.name ;
    }

    public Integer getStatus() {
        return this.status ;
    }
}
