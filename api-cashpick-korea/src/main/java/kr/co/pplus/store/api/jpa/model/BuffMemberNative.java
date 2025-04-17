package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "buffMemberNative")
@Table(name = "buff_member")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffMemberNative implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "buff_seq_no")
    private Long buffSeqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Column(name = "is_owner")
    private Boolean isOwner;

    @Column(name = "received_bol")
    private Float receivedBol;

    @Column(name = "divided_bol")
    private Float dividedBol;

    @Column(name = "received_point")
    private Float receivedPoint;

    @Column(name = "divided_point")
    private Float dividedPoint;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "is_friend")
    private Boolean isFriend;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "buff_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    Buff buff = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    Member member = null ;

}
