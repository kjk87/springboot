package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name = "buyCustomer") // This tells Hibernate to make a table out of this class
@Table(name = "buy_customer")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyCustomer {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	Long seqNo;

	@Column(name="member_seq_no")
	Long memberSeqNo;

	@Column(name="page_seq_no")
	Long pageSeqNo;

	@Column(name="buy_count")
	Integer buyCount;

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name="last_buy_datetime")
	String lastBuyDatetime;

}
