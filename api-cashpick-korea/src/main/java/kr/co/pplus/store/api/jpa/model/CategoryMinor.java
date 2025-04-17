package kr.co.pplus.store.api.jpa.model;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="categoryMinor")
@Table(name="category_minor")
public class CategoryMinor {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq_no")
	private Long seqNo;
	private Long major;
	private String name;
	private String status;
	private Integer array;
	@Column(name="reg_datetime")
	private Date regDatetime;
	@Column(name="mod_datetime")
	private Date modDatetime;
	private String register;
	private String updater;
	private String image;


	@NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "major", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private CategoryMajorOnly categoryMajor;
	
}
