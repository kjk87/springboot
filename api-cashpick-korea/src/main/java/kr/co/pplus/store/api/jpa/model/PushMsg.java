package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="pushMsg") // This tells Hibernate to make a table out of this class
@Table(name="push_msg")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushMsg {

    public PushMsg(){

    }

    @Id
    @Column(name="seq_no")
    String seqNo ;

    @Column(name="page_seq_no")
    Long pageSeqNo ;

    @Column(name="app_type")
    String appType ;

    Boolean reserved  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reserve_date", updatable = false)
    private String reserveDate;

    String subject  = null ;

    String contents  = null ;

    @Column(name="move_type1")
    String moveType1  = null ;

    @Column(name="move_type2")
    String moveType2  = null ;

    @Column(name="move_seq_no")
    Long moveSeqNo  = null ;

    @Column(name="target_count")
    Long targetCount  = null ;

    @Column(name="success_count")
    Integer successCount  = null ;

    @Column(name="fail_count")
    Integer failCount  = null ;

    Integer status  = null ;//1:발송 2:발송취소 0:발송전

    @Column(name="use_free")
    Integer useFree  = null ;

    @Column(name="use_cash")
    Integer useCash  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    private String regDatetime;

    String age  = null ;

    String gender  = null ;

    String buy  = null ;

    String start  = null ;

    String end  = null ;

    String note  = null ;

    Boolean free  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="cancel_datetime", updatable = false)
    private String cancelDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="complete_datetime", updatable = false)
    private String completeDatetime;

}
