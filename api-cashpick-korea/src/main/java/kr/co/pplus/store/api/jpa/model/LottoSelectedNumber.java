package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="lottoSelectedNumber") // This tells Hibernate to make a table out of this class
@Table(name="lotto_selected_number")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LottoSelectedNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Integer seqNo;

    @Column(name = "event_seq_no")
    Long eventSeqNo;

    @Column(name = "member_seq_no")
    Long memberSeqNo;

    @Column(name = "event_join_seq_no")
    Long eventJoinSeqNo;

    @Column(name = "lotto_number")
    Integer lottoNumber;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Column(name= "is_accord")
    Boolean isAccord ;
}