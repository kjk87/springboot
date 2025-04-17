package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Map;

@Entity(name="bolHistory") // This tells Hibernate to make a table out of this class
@Table(name="bol_history")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BolHistory {


    public BolHistory() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Long seqNo; // '순번',


    @Column(name = "member_seq_no")
    Long memberSeqNo = null;  //사용자 순번

    @Column(name = "page_seq_no")
    Long pageSeqNo = null;


    @Column(name = "primary_type")
    String primaryType = null;

    @Column(name = "secondary_type")
    String secondaryType = null;

    @Column(name = "refund_status")
    String refundStatus = null;

    @Column(name = "amount")
    Float amount = null;

    @Column(name = "subject")
    String subject = null;

    @Column(name = "target_type")
    String targetType = null;

    @Column(name = "target_seq_no")
    Long targetSeqNo ;


    @Convert(converter = JpaConverterJson.class)
    @Column(name = "history_prop")
    Map<String, Object> historyProp = null;  //short URL unique key

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    @Column(name = "is_lotto_ticket")
    Boolean is_lotto_tiket = false;

}