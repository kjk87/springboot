package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="pageAddress")
@Table(name="page_address")
public class PageAddress {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
	private Long seqNo;
	
	@Column(name="page_seq_no")
	private Long pageSeqNo;
	private String name;
	private String postcode;
	private String addr1;
	private String addr2;
	private String tel;
	private Integer type; // 1:대표출고지, 2:대표반품교환지, 3:일반주소 

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name = "reg_datetime")
	String regDatetime;
}
