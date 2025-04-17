package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="categoryThird")
@Table(name="category_third")
public class CategoryThird {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	private Long first;
	private Long second;
	private String name;
	private String status; // active, passive
	private Integer array;

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name = "reg_datetime")
	String regDatetime;

	@Convert(converter = JpaConverterDatetime.class)
	@Column(name = "mod_datetime")
	String modDatetime;

	private String register;
	private String updater;
	
}
