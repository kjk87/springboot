package kr.co.pplus.store.api.jpa.model.reappay;

import lombok.Data;

import java.util.Map;


@Data
public class ReapPayRes {

    String code;

    Integer status;

    String message;

    Long timestamp;

    Map<String, Object> content;
}
