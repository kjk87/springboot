package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="notificationBox")
@Table(name="notification_box")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationBox {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo = null ;

    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    String subject = null;

    String contents = null;


    @Column(name="move_type1")
    String moveType1 = null ;

    @Column(name="move_type2")
    String moveType2 = null ;

    @Column(name="move_seq_no")
    Long moveSeqNo = null ;

    @Column(name="move_string")
    String moveString = null ;


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="read_datetime")
    String readDatetime  = null ;

    @Column(name = "is_read")
    Boolean isRead = null;

}
