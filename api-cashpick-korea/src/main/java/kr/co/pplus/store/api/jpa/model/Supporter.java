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
import java.io.Serializable;
import java.util.Date;


@Entity(name="supporter")
@Table(name="supporter")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Supporter implements Serializable{

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	@Column(name="member_seq_no")
	private Long memberSeqNo;
	private String telegram;
	private String twitter;
	private String status; // pending(신청),return(반려),normal(승인),redemand(재신청)
	private String reason;

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name = "reg_datetime")
	private String regDatetime;

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name = "status_datetime")
	private String statusDatetime;

}
