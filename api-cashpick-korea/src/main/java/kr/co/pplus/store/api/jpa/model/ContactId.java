package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterMobileNumber;

import javax.persistence.Convert;
import java.io.Serializable;

public class ContactId implements Serializable {

    private Long memberSeqNo;

    private String mobileNumber;

    public ContactId() {
    }

    public ContactId(Long memberSeqNo, String mobileNumber) {
        this.memberSeqNo = memberSeqNo;
        this.mobileNumber = mobileNumber;
    }
}