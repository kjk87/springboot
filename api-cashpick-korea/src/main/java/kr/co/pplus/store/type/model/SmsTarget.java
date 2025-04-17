package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("SmsTarget")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsTarget extends MsgTarget {

	private static final long serialVersionUID = 3707536650371249887L;

	private String mobile;
	//Extended from MsgTarget
//	private String name;
	private Customer customer;
	private User user;
	private Integer smsPrice;
}
