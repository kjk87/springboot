package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="giftishow")
@Table(name="giftishow")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Giftishow {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
	private Long seqNo ; // '상품 구매 순번',

    @Column(name="goods_code")
    private String goodsCode = null ;

    @Column(name="goods_no")
    private String goodsNo = null ;

    @Column(name="goods_name")
    private String goodsName = null ;

    @Column(name="brand_code")
    private String brandCode = null ;

    @Column(name="brand_name")
    private String brandName = null ;

    private String content = null ;

    @Column(name="content_add_desc")
    private String contentAddDesc = null ;

    @Column(name="discount_rate")
    private Float discountRate = null ;

    @Column(name="goods_type_nm")
    private String goodsTypeNm = null ;

    @Column(name="goods_img_s")
    private String goodsImgS = null ;

    @Column(name="goods_img_b")
    private String goodsImgB = null ;

    @Column(name="goods_desc_img_web")
    private String goodsDescImgWeb = null ;

    @Column(name="brand_icon_img")
    private String brandIconImg = null ;

    @Column(name="mms_goods_img")
    private String mmsGoodsImg = null ;

    @Column(name="discount_price")
    private String discountPrice = null ;

    @Column(name="real_price")
    private Integer realPrice = null ;

    @Column(name="sale_price")
    private String salePrice = null ;

    @Column(name="srch_keyword")
    private String srchKeyword = null ;

    @Column(name="valid_prd_type_cd")
    private String validPrdTypeCd = null ;

    @Column(name="limit_day")
    private String limitDay = null ;

    @Column(name="valid_prd_day")
    private String validPrdDay = null ;

    @Column(name="end_date")
    private Date endDate = null ;

    @Column(name="goods_com_id")
    private String goodsComId = null ;

    @Column(name="goods_com_name")
    private String goodsComName = null ;

    @Column(name="affiliate_id")
    private String affiliateId = null ;

    private String affiliate = null ;

    @Column(name="exh_gender_cd")
    private String exhGenderCd = null ;

    @Column(name="exh_age_cd")
    private String exhAgeCd = null ;

    @Column(name="mms_reserve_flag")
    private String mmsReserveFlag = null ;

    @Column(name="goods_state_cd")
    private String goodsStateCd = null ;

    @Column(name="mms_barcd_create_yn")
    private String mmsBarcdCreateYn = null ;

    @Column(name="rm_cnt_flag")
    private String rmCntFlag = null ;

    @Column(name="sale_date_flag_cd")
    private String saleDateFlagCd = null ;

    @Column(name="goods_type_dtl_nm")
    private String goodsTypeDtlNm = null ;

    @Column(name="category1_seq")
    private String category1Seq = null ;

    @Column(name="category2_seq")
    private String category2Seq = null ;

    @Column(name="sale_date_flag")
    private String saleDateFlag = null ;

    @Column(name="rm_id_buy_cnt_flag_cd")
    private String rmIdBuyCntFlagCd = null ;

    @Column(name="md_code")
    private String mdCode = null ;


    private Boolean sale = null ;

    private Integer priority = 1 ;

    @Column(name = "giftishow_category_seq_no")
    private Long giftishowCategorySeqNo;

    @Column(name = "brand_seq_no")
    private Long brandSeqNo;


}
