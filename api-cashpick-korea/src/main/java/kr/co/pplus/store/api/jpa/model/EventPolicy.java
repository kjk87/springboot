package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="eventPolicy") // This tells Hibernate to make a table out of this class
@Table(name="event_policy")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Long seqNo;

    @Column(name = "event_seq_no")
    Long eventSeqNo;

    @Column(name = "page_seq_no")
    Long pageSeqNo;

    String title ;

    String url ;
}