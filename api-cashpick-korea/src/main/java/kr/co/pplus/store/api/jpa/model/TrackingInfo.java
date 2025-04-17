package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackingInfo {

    String adUrl;// 택배사에서 광고용으로 사용하는 주소
    String complete;// 배송 완료 여부(true or false)
    String completeYN;// 배송 완료 여부 (Y,N)
    String estimate;// 배송예정 시간
    TrackingDetail firstDetail;//
    String invoiceNo;// 운송장 번호
    String itemImage;// 상품이미지 url
    String itemName;// 상품 이름
    TrackingDetail lastDetail;//
    TrackingDetail lastStateDetail;//
    String level;// 진행단계 [level 1: 배송준비중, 2: 집화완료, 3: 배송중, 4: 지점 도착, 5: 배송출발, 6:배송 완료]
    String orderNumber;// 주문 번호
    String productInfo;// 상품정보
    String result;// 조회 결과
    List<TrackingDetail> trackingDetails;// description:상세 정보
    String zipCode;// zipCode
}
