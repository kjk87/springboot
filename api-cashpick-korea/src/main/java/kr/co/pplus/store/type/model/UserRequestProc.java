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
@Alias("UserRequestProc")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequestProc extends AbstractModel {

	private static final long serialVersionUID = -3174030250555190009L;

	private UserRequest request;
	private Integer no;
	private String prevStatus;
	private String procStatus;
	private String note;
	private User actor;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
}
