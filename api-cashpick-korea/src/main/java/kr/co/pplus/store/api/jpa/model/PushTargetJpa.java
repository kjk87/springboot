package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="pushTarget") // This tells Hibernate to make a table out of this class
@Table(name="push_target")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@IdClass(PushTargetId.class)
public class PushTargetJpa {

    @Id
    @Column(name = "msg_seq_no")
    Long msgSeqNo;

    @Id
    @Column(name = "member_seq_no")
    Long memberSeqNo;

    @Column(name = "confirm_prop")
    String confirmProp;

    @Column(name = "status")
    String status;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name= "readed")
    Boolean readed ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "send_datetime")
    private String sendDatetime;

    @Column(name = "push_price")
    private Integer pushPrice;


}