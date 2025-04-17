package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("InicisNotificaiton")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InicisNotificaiton extends AbstractModel {
	private static final long serialVersionUID = -3392406721421487986L;

	private String P_STATUS;
	private String P_TID;
	private String P_TYPE;
	private String P_AUTH_DT;
	private String P_MID;
	private String P_OID;
	private String P_FN_CD1;
	private String P_FN_CD2;
	private String P_FN_NM;
	private String P_AMT;
	private String P_UNAME;
	private String P_RMESG1;
	private String P_RMESG2;
	private String P_RMESG3;
	private String P_NOTI;
	private String P_AUTH_NO;
	private String P_CARD_ISSUER_CODE;
	private String P_CARD_NUM;
	private String P_CARD_MEMBER_NUM;
	private String P_CARD_PURCHASE_CODE;
	private String P_PRTC_CODE;
	private String P_SRC_CODE;
	private String P_ISP_CARDCODE;
	private String P_CARD_PURCHASE_NAME;
	private String P_CARD_ISSUER_NAME;
	private String P_MERCHANT_RESERVED;
	private String P_CSHR_AMT;
	private String P_CSHR_SUP_AMT;
	private String P_CSHR_TAX;
	private String P_CSHR_SRVC_AMT;
	private String P_CSHR_TYPE;
	private String P_CSHR_IT;
	private String P_CSHR_AUTH_NO;
	
}
