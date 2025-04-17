package kr.co.pplus.store.api.jpa.model;

public enum GiftStatus {
    WAIT( 0),
    USE(1),
    REVIEW_WRITE(2),
    EXPIRED(3);

    private final int status;

    private GiftStatus(int status)
    {
        this.status = status;
    }

    public int getStatus() {
        return this.status ;
    }
}
