package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name="MemberAddress") // This tells Hibernate to make a table out of this class
@Table(name="member_address")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberAddress {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo;

    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    String name;

    @Column(name="post_code")
    String postCode;

    String address;

    @Column(name="address_detail")
    String addressDetail;

    String tel;

}
