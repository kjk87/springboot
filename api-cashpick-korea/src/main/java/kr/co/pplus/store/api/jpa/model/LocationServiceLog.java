package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="locationServiceLog")
@Table(name="location_service_log")
public class LocationServiceLog {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	private String id;
	private String platform; // aos, ios

	@Column(name = "service_log")
	private String serviceLog;

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name = "reg_datetime")
	String regDatetime;

}
