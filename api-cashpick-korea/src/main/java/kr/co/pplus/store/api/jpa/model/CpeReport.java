package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="cpeReport")
@Table(name="cpe_report")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CpeReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name="reg_datetime")
	private String regDatetime;

	private String type;
	private String id;
}
