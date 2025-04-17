package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderMessage {
    private String pageSeqNo;
    private String seqNo;
    private String type;//request, cancel
    private String salesType;
    private String status;
    private String payMethod;
    private String title;
    private String price;
    private String amount;
    private String address;
    private String addressDetail;
    private String visitTime;
    private String visitNumber;
    private String regDatetime;
}
