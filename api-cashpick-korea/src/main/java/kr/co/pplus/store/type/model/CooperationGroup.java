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
@Alias("CooperationGroup")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CooperationGroup extends NoOnlyKey {
	
	private static final long serialVersionUID = -2718483331987815016L;

	private String name;
	private String status;
	private Integer priority;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private Attachment icon;
	
	public void setGroupNo(Long no) {
		setNo(no);
	}
}
