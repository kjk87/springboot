package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "buffRequest")
@Table(name = "buff_request")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "buff_seq_no")
    private Long buffSeqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    private Long requester;

    private String status;//request,reject,consent,withdraw

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "status_datetime")
    private String statusDatetime;

    @Column(name = "withdraw_type")
    private String withdrawType;// compulsory(강퇴), oneself(스스로)

    private String note;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "buff_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    Buff buff = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "requester",  referencedColumnName="seq_no", insertable=false, updatable=false)
    Member member = null ;
}
