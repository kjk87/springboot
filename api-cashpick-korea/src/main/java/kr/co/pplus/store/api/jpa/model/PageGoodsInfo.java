package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;


@Entity(name="pageGoodsInfo")
@Table(name="page_goods_info")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageGoodsInfo {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    @Column(name="page_seq_no")
    Long pageSeqNo ;

    @Column(name="goods_seq_no")
    Long goodsSeqNo ; // 0 이면 스토어 페이지 기본 상품 고시 정보 값

    @Column(name="category")
    String category ; // 상품정보 카테고리명

    @Convert(converter = JpaConverterJson.class)
    @Column(name="info_prop")
    List<GoodsInfo> infoPropList  = null ; //'상품 고시 정보'

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    String regDatetime  = null ; // '등록 시각'

    /*
    //필수항목
    String name ;  // 메뉴명 또는 품명 및 모델명
    String dimensions ; //크기, 중량, 치수, 수량
    String maker ; //제조자 및 판매자
    String originCountry ; // 제조국(원산지)
    String qualityStandard ; // 품짋보증기준
    String customerHelpline ; // 소비자 상담전화(A/S 책임자 잔화번호)
    String handlingCautions; // 취급시 주의 사항


    //제품상세 1
    String composition ; //제품 종류 및 구성
    String ingredients ; //소재 및 성분
    String highlights ; //제품 주요 사항
    String color ; //제품 색상
    String manufactureDate ; //제조년월일
    String validPeriod ; // 사용기간, 유효기간, 품질 유지 기간

    //제품상세 2
    String voltagePower ; //전격전압, 소비전력
    String energyEfficiency ; //에너지 소비 효율 등급
    String usingPurpose ; //제품의 사용 목적 및 방법

    //부가 정보 (기타 기술 방식)
    String installCosts ; // 배송 및 설치 비용
    String coverArea ; // 냉난방 면적
    String joinCondition ; // 가입 조건
    String applicableModel; // 적용 차종
    String updateFee ; // 맵 업데이트 비용
    String freePeriod; // 무상 기간

    //관련법령 심사 필 유무 or 등록 번호
    String permissionNumber ; // 허가및 신고 번호
    String relatedLaws ; //관련법상 표시사항
    String KcCertification ; // KC 인증 필 유무
    String gmoFoodLabel ; //유전자변형 건강기능 식품 표시
    String cosmeticFdsReview ; //화장품법에 따른 식품의약품안전처 심사 필
    String warranty ; //보증서 제고 유무
    String advertiseReview ; //표시광고 사전심의 필
    String importDeclaration ; //식품위생법에 따른 수입신고 필
    String noMedicineDesc ; // 질병의 예방 및 치료를 위한 의약품이 아니라는 표현
    */


}
