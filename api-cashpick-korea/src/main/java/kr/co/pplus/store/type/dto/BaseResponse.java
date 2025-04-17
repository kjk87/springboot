package kr.co.pplus.store.type.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    public BaseResponse(){

    }

    Integer resultCode;

    T row;


}
