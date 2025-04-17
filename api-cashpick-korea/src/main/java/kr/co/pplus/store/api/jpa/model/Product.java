package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Entity(name="product")
@Table(name="product")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;
	
    @Column(name="page_seq_no")
    private Long pageSeqNo; // '상품 상점 페이지 순번',
    
    @Column(name="market_type")
    private String marketType; // retail, wholesale
    
    @Column(name="sales_type")
    private Long salesType; // 판매분류 - 1:매장판매, 2:배달, 3:배송, 4:예약, 5:픽업, 6:티켓, 7:구독권, 8:금액권
    
    @Column(name="status")
    private Integer status; //'상품상태 1:판매중, 0:판매완료 soldout, -1:판매종료(expire), -2:판매중지, -999: 삭제
    
    private Long first; // 1차 카테고리
    private Long second; // 2차 카테고리
    private Long third; // 3차 카테고리

    @Column(name="name")
    private String name; //'상품명',

    @Column(name="price_method")
    private String priceMethod; // 도매상품시 판매가 고정여부 (free, fix)
    
    private Boolean surtax; // 과세상품여부 (1:과세, 0:면세)
    
    @Column(name="sales_term")
    private Boolean salesTerm; // 판매기간설정여부(1,0)

    @Column(name = "start_date")
    Date startDate;

    @Column(name = "end_date")
    Date endDate;
    
    private String contents; // 상세정보
    
    private Integer count; //'상품 수량  -1 : 수량제한 없음',

    @Column(name="sold_count")
    private Integer soldCount; //'상품 수량  -1 : 수량제한 없음',

    @Column(name="use_option") // 옵션사용여부
    private Boolean useOption;
    
    @Column(name="option_type")
    private String optionType; // single, union
    
    @Column(name="option_array")
    private String optionArray; // 옵션 정렬순서 (등록순:regDate, 가나다:alphabet, 높은가격:highPrice, 낮은가격:lowPrice)
    
    @Column(name="is_kc")
    private Boolean isKc; 
    @Column(name="non_kc_memo")
    private String nonKcMemo; // 구매대행, 안전기준준수, KC안전관리대상 아님
    
    @Column(name="notice_group")
    private String noticeGroup; // 상품고시 상품군 
    
    private Boolean blind = false; // 관리자가 미노출처리하는 값
    private String reason; // blind 제재사유

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    String regDatetime;
    
    @Column(name="register_type")
    private String registerType; // 상품등록자 구분(user/admin)
    
    private String register; // 등록자

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    String modDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "status_datetime")
    String statusDatetime;

    @Column(name = "wholesale_company")
    String wholesaleCompany;

    @Column(name = "original_seq_no")
    String originalSeqNo;

    @Column(name = "supplier_seq_no")
    Long supplierSeqNo;

    String origin;

    String notice;

    @Column(name = "sub_name")
    String subName;

    @Column(name="change_enable")
    private Boolean changeEnable;

    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "first", referencedColumnName = "seq_no", insertable = false, updatable = false)
	private CategoryFirst categoryFirst;
    
    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "second", referencedColumnName = "seq_no", insertable = false, updatable = false)
	private CategorySecond categorySecond;
    
    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "third", referencedColumnName = "seq_no", insertable = false, updatable = false)
	private CategoryThird categoryThird;
    
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name="product_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @OrderBy("deligate desc, array asc")
    private Set<ProductImage> imageList;
}
