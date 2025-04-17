package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="lottoMemberCount") // This tells Hibernate to make a table out of this class
@Table(name="lotto_member_count")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LottoMemberCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Integer seqNo;

    @Column(name = "event_seq_no")
    Long eventSeqNo;

    @Column(name = "member_seq_no")
    Long memberSeqNo;

    @Column(name = "event_join_seq_no")
    Long eventJoinSeqNo;

    @Column(name = "correct_count")
    Integer correctCount;

    @Column(name = "is_winner")
    Boolean isWinner;


}