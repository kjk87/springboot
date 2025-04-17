package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BolRefundProc")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BolRefundProc extends AbstractModel {

	private static final long serialVersionUID = -6446724034783194373L;
	private BolHistory history;
	private Integer procNo;
	private String prevStatus;
	private String procStatus;
	private String reason;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;

}
