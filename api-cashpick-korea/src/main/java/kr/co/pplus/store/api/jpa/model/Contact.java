package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.util.SecureUtil;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "contact")
@Table(name = "contact")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@IdClass(ContactId.class)
public class Contact implements Serializable {

    @Id
    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Id
    @Column(name = "mobile_number")
    private String mobileNumber;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    public String getMobileNumber() {
        return SecureUtil.decryptMobileNumber(mobileNumber);
    }
}
