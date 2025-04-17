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
@Alias("FaqGroup")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FaqGroup extends NoOnlyKey {

	private static final long serialVersionUID = -1527701342429492331L;

	private String name;
	private String status;
	private Integer priority;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private User modUser;
	
	public void setGroupNo(Long no) {
		setNo(no);
	}
}
