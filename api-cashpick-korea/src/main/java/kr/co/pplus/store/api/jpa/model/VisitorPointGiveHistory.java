package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity(name="visitorPointGiveHistory") // This tells Hibernate to make a table out of this class
@Table(name="visitor_point_give_history")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitorPointGiveHistory {


    public VisitorPointGiveHistory() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Long seqNo;

    @Column(name = "page_seq_no")
    Long pageSeqNo = null;

    @Column(name = "sender_seq_no")
    Long senderSeqNo = null;

    @Column(name = "receiver_seq_no")
    Long receiverSeqNo = null;

    String type = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    Integer price = null;

    @Column(name = "is_payment")
    Boolean isPayment = false;

    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ;

    @Transient
    String authCode;

    @Transient
    String echossId;

    @Transient
    String token;

}