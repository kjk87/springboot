package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffMsg extends AbstractModel {


	private Long buffSeqNo;
	private Long dividerSeqNo;
	private Float amount;
	private String moneyType;//bol, point
	private String type;//event, lotto, shopping
	private Long eventSeqNo;
	private Long shoppingSeqNo;
	private Float winPrice;
	private String image;
	private String title;

}
