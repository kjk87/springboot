package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("CashLog")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashLog extends AbstractModel {
	private Long seqNo;
	private Long memberSeqNo;
	private Long pageSeqNo;
	private String type;
	private Integer cash;
	private String note;
	private Date regDatetime;

}
