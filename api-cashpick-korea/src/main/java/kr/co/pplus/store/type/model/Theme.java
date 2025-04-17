package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.ActiveStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Theme")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Theme extends NoOnlyKey {

	private static final long serialVersionUID = -7659557919143151811L;

	private String name;
	private Integer priority;
	private ActiveStatus status;
	private Long clickCount;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private User modUser;
	
	public void setThemeNo(Long no) {
		super.setNo(no);
	}
}
