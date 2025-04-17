package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterMobileNumber;
import kr.co.pplus.store.util.SecureUtil;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "contactWithMember")
@Table(name = "contact")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@IdClass(ContactId.class)
public class ContactWithMember implements Serializable {

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

    @Formula("(select count(1) from member m where m.mobile_number = mobile_number) > 0")
    Boolean isMember;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "mobile_number", referencedColumnName = "mobile_number", insertable = false, updatable = false)
    MemberWithBuffMember member = null;


    public String getMobileNumber() {
        return SecureUtil.decryptMobileNumber(mobileNumber);
    }
}
