package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BuffDividedBolLog")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffDividedBolLog extends AbstractModel {

	private Long seqNo;

	private Long buffSeqNo;

	private Long memberSeqNo;

	private String type;//event, lotto, shopping

	private String moneyType;//bol, point

	private Long eventSeqNo;

	private Long shoppingSeqNo;

	private Float amount;

	private Date regDatetime;
	
}
