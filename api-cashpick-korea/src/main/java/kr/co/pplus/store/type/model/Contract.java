package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.ContractStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Contract")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contract extends NoOnlyKey {

	private static final long serialVersionUID = 3125439143734488543L;
	
	private ContractStatus status;
	private Duration duration;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User user;
}
