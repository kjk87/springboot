package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="giftishowCategory")
@Table(name="giftishow_category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftishowCategory {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
	private Long seqNo ;

    private String name = null ;

    private String status = null ;

    private Integer priority = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;
}
