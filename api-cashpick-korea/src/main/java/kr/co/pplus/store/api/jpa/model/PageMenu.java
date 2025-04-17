package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="pageMenu")
@Table(name="page_menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageMenu {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;
	
    @Column(name="page_seq_no")
    private Long pageSeqNo;

    @Column(name="status")
    private Integer status; //'상품상태 1:판매중, 0:판매완료 soldout, -1:판매종료(expire), -2:판매중지, -999: 삭제

    private Boolean blind = false; // 관리자가 미노출처리하는 값

    private String reason; // blind 제재사유

    @Column(name="name")
    private String name; //'상품명',

    @Column(name = "origin_price")
    Integer originPrice;

    Integer price;

    String thumbnail;

    @Column(name = "discount_ratio")
    Float discountRatio;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    String modDatetime;

}
