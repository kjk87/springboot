package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="pushOpenLog")
@Table(name="push_open_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushOpenLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="msg_seq_no")
    private String msgSeqNo;
    @Column(name="member_seq_no")
    private Long memberSeqNo;
    private String device; // android, ios, windows ..
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;
}
