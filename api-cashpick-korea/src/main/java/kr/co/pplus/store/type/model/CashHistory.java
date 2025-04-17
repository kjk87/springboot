package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("CashHistory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashHistory extends NoOnlyKey {

	private static final long serialVersionUID = 5377809843784897570L;

	private Long seqNo;
	private Long memberSeqNo;
	private String type;
	private String secondaryType;
	private Float cash;
	private String subject;
	private Map<String, Object> historyProp;
	private Date regDatetime;

}
