package kr.co.pplus.store.api.jpa.model.reappay;

import lombok.Data;


@Data
public class ReapPayBillKeyData {

    String billkeytid;
    String billkeytotAmt;
    String billkeyvatAmt;
    String billkeysplAmt;
    String billkeyapprovalNumb;
    String billkeytradeDateTime;
    String billkeyrespCode;
    String billkeyrespMessage;
    String billkeyissuerCardType;
    String billkeyissuerCardName;
    String billkeypurchaseCardType;
    String billkeypurchaseCardName;
    String billkeymaskedCardNumb;
    String billkeycardType;
    String billkeybillingToken;
    String billkeyTranseq;
    String billkeyinstallment;
    String billkeyOrderNo;
    String billkeypayload;


}
