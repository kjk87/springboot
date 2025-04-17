package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;


@Data
public class FTLinkAddRequest {

    String compcode ;//서비스코드

    String distid ;//총판아이디

    String shopname ;

    String loginid ;

    String loginpwd ;

    String shop_type; //1 : 개인사업자, 2: 법인사업자

    String shop_bizowner ; //대표자명

    String shop_cust_tel ; //연락처

    String shop_bizname ; //사업자명

    String shop_bizno ;//사업자번호

    String shop_bizaddr ; //사업자주소

    String shop_cust_hp ; //담당자 휴대폰번호

    String shop_saletype ; //0: 온라인, 1:오프라인, 2:온오프라인
}
