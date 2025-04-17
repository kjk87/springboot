package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="categoryMajorOnly")
@Table(name="category_major")
public class CategoryMajorOnly {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	private String name;
	private String type; // online, offline, common
	private String status; // active, passive
	private Integer array;
	@Column(name="reg_datetime")
	private Date regDatetime;
	@Column(name="mod_datetime")
	private Date modDatetime;
	private String register;
	private String updater;
	private String image;
	@Column(name="background_image")
	private String backgroundImage;
	
}
