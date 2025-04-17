package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity(name="wholesaleBalance") // This tells Hibernate to make a table out of this class
@Table(name="wholesale_balance")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WholesaleBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "issue_date")
    private LocalDate issueDate;
    @Column(name = "agent_seq_no")
    private Long agentSeqNo;
    @Column(name = "total_price")
    private Float totalPrice = 0f;
    @Column(name = "advertise_ratio")
    private Float advertiseRatio = 0f;
    @Column(name = "advertise_fee")
    private Float advertiseFee = 0f;
    private String status; // ready, complete, hold

    @Column(name = "status_datetime")
    private LocalDateTime statusDatetime;

    private String note;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;


    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "agent_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private Agent agent;

}