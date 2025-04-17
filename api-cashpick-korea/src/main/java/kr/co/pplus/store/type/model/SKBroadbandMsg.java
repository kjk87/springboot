package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("SKBroadbandMsg")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SKBroadbandMsg extends AbstractModel {

	private static final long serialVersionUID = 4293882431377422920L;

	private Integer cmpMsgId;
	private String cmpMsgGroupId;
	private String usrId;
	private String smsGb;
	private String usedCd;
	private String reservedFg;
	private String reservedDttm;
	private String savedFg;
	private String rcvPhnId;
	private String sndPhnId;
	private String natCd;
	private String assignCd;
	private String sndMsg;
	private String callbackUrl;
	private Integer contentCnt;
	private String contentMimeType;
	private String contentPath;
	private String cmpSndDttm;
	private String cmpRcvDttm;
	private String regSndDttm;
	private String regRcvDttm;
	private String machineId;
	private String smsStatus;
	private String rsltVal;
	private String msgTitle;
	private String telcoId;
	private String etcChar1;
	private String etcChar2;
	private String etcChar3;
	private String etcChar4;
	private Integer etcInt5;
	private Integer etcInt6;
	
}
