package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="msg")
@Table(name="msg")
public class MsgJpa {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo = null ;

    @Column(name="input_type")
    String inputType = null ;

    @Column(name="msg_type")
    String msgType = null ;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="reserved")
    Boolean reserved = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reserve_date")
    String reserveDate = null;

    String status = null ;
    String subject = null;
    String contents = null;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="include_me")
    Boolean includeMe = null ;

    @Column(name="move_type1")
    String moveType1 = null ;

    @Column(name="move_type2")
    String moveType2 = null ;

    @Column(name="move_seq_no")
    Long moveSeqNo = null ;

    @Column(name="move_string")
    String moveString = null ;

    @Column(name="msg_prop")
    String msgProp = null ;

    @Column(name="target_count")
    Integer targetCount = null ;

    @Column(name="succ_count")
    Integer succCount = null ;

    @Column(name="fail_count")
    Integer failCount = null ;

    @Column(name="read_count")
    Integer readCount = null ;

    @Column(name="pay_type")
    String payType = null ;

    @Column(name="total_price")
    Long totalPrice = null ;

    @Column(name="refund_price")
    Long refundPrice = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="complete_datetime")
    String completeDatetime  = null ;

    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Column(name="page_seq_no")
    Long pageSeqNo = null;
}
