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
@Alias("Report")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report extends NoOnlyKey {

	private static final long serialVersionUID = 6527440479692061296L;

	private User reporter;
	private NoOnlyKey target;
	private String targetType;
	private String reason;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	
	public void setReportNo(Long no) {
		setNo(no);
	}
}
