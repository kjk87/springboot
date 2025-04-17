package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("SmsMsg")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsMsg extends AbstractModel {

	private static final long serialVersionUID = 2182758791129246536L;

	private String sender;
	private String receiver;
	private String msg;
}
