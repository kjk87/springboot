package kr.co.pplus.store.api.jpa.model.reappay;

import lombok.Data;


@Data
public class ReapPayLoginData {

    String access_token ;//인증을 제외한 모든 곳에서 사용

    Integer expires_in ;//발급 시각 기준 36000 후 만료

    String jti ;

    String refresh_token ;

    String scope ;

    String token_type;
}
