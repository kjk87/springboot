package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="plus") // This tells Hibernate to make a table out of this class
@Table(name="plus")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlusOnly {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // 'Plus 순번',

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="block")
    Boolean block = false ; //`

    @Column(name="member_seq_no")
    Long memberSeqNo ; // 'Plus 사용자  순번',

    @Column(name="page_seq_no")
    Long pageSeqNo ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime ;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="push_activate")
    private Boolean pushActivate;

    @Column(name="buy_count")
    private Integer buyCount;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="last_buy_datetime")
    private String lastBuyDatetime;




}
