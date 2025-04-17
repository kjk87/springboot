package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackingDetail {

    String code;// 배송상태 코드
    String kind;// 진행상태
    String level;// 진행단계
    String manName;// 배송기사 이름
    String  manPic;// string 배송기사 전화번호
    String remark;// 비고
    String telno;//진행위치(지점)전화번호 (json 요청시)
    String telno2;// 배송기사 전화번호 (json 요청시)
    String time;// 진행시간 (json 요청시)
    String timeString;// 진행시간
    String trans_telno;// 진행위치(지점)전화번호 (xml 요청시)
    String trans_telno2;// 배송기사 전화번호 (xml 요청시)
    String trans_time;// 진행시간 (xml 요청시)
    String trans_where;// 진행위치지점 (xml 요청시)
    String where;// 진행위치지점 (json 요청시)
}
