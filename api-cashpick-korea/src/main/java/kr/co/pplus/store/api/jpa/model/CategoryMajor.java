package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity(name="categoryMajor")
@Table(name="category_major")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryMajor {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	private String name;
	private String type; // online, offline, none
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
	
	@OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@Where(clause = "status = 'active'")
	@OrderBy("array asc, reg_datetime desc")
	@JoinColumn(name = "major", referencedColumnName = "seq_no", insertable = false, updatable = false)
	private List<CategoryMinor> minorList = new ArrayList<>();
	
}
