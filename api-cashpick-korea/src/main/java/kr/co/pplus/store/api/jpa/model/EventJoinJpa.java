package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity(name="eventJoin") // This tells Hibernate to make a table out of this class
@Table(name="event_join")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventJoinJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_seq_no")
    Long eventSeqNo;

    @Column(name = "seq_no")
    Integer seqNo;

    @Column(name = "member_seq_no")
    Long memberSeqNo;

    @Column(name = "join_datetime")
    Date joinDatetime;

    @Column(name= "join_prop")
    String joinProp ;

    @Column(name= "win_code")
    String winCode ;

    @Column(name= "is_buy")
    Boolean isBuy ;

    @Column(name = "event_buy_seq_no")
    Long eventBuySeqNo;
}