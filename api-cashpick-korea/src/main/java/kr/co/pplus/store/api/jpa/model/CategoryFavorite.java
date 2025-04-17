package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="CategoryFavorite")
@Table(name="category_favorite")
public class CategoryFavorite {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="member_seq_no")
	private Long memberSeqNo;

	@Column(name="category_minor_seq_no")
	private Long categoryMinorSeqNo;

	@Column(name="category_major_seq_no")
	private Long categoryMajorSeqNo;

	@OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="category_minor_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
	CategoryMinor categoryMinor  = null ;
	
}
