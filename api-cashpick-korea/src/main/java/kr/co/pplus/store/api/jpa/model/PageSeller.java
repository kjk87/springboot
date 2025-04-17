package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashMap;

@Entity(name="pageSeller") // This tells Hibernate to make a table out of this class
@Table(name="page_seller")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageSeller {

    public PageSeller(){

    }

    @Id
    @Column(name = "page_seq_no", nullable = false)
    Long pageSeqNo = null ;

    @Key
    @Column(name = "member_seq_no", nullable = false)
    Long memberSeqNo = null ;

    @Column(name="biz_email", nullable = false)
    String bizEmail = null ; // '상점주 통장사본 은행명',

    @Column(name="biz_bank_code")
    String bizBankCode = null ; // '상점주 통장사본 은행명(Code)',

    @Column(name="biz_bank_book_no")
    String bizBankBookNo = null ; // '상점주 통장사본 계좌번호',

    @Column(name="biz_bank_book_owner")
    String bizBankBookOwner = null ; // '상점주 통장사본 소유주',

    @Column(name="biz_name")
    String bizName = null ; // '상점명',

    @Column(name="biz_owner")
    String bizOwner = null ; // '상점주 이름',

    @Column(name="biz_reg_no", nullable = false, unique=true)
    String bizRegNo = null ; // '상점 사업자 번호',


    @Column(name="biz_address")
    String bizAddress = null ; // '상점 주소',

    @Column(name="biz_type")
    String bizType = null ; // '상점 업태',

    @Column(name="biz_category")
    String bizCategory = null ; // '상점 종목',


    @Convert(converter = JpaConverterJson.class)
    @Column(name="biz_prop")
    HashMap<String, Object> bizProp  = null ; // '상점 옵션 : 사업자등록증 이미지 ID, 통장사본 이미지 ID 포함',


    @Column(name="biz_cancel_msg")
    String bizCancelMsg = null ; // '상점 등록 취소/중단 사유',

    @Column(name="biz_pay_ratio")
    Float bizPayRatio = null ; // '상점 구매 수수료율',

    @Column(name="is_seller")
    Boolean isSeller = false ; // '상점 등록 인증 완료 여부',

    @Column(name="status")
    Integer status = 0 ; // 0 : 판매자 미인증, 1: 판매자 인증완료, 2: 판매자 인증대기, 3: 판매자 인증반려

    @Column(name="is_terms_accept")
    Boolean isTermsAccept = false ; // '상점 약관 동의 여부',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; // '장바구니 상품 등록 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; // '장바구니 상품 변경 시각',

    @Column(name="origin_desc")
    String originDesc = null ; // 상점 전체 원산지 표기 설명

    @Column(name="biz_owner_reg_no")
    String bizOwnerRegNo ;

    @Column(name="ecommerce_reg_no")
    String ecommerceRegNo ;

    @Column(name="hello_mid")
    String helloMid ;

    @Column(name="tid_mid")
    String tidMid ;

    @Column(name="project_code")
    String projectCode ;

}
