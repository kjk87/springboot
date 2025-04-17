package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageGroup")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageGroup extends NoOnlyKey {
	private String name;
	private String status;
	private String platform;
	private Integer pageCount;
	private Integer priority;
	private Map<String, Object> properties;
	private List<Page> pageList;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;
	
	public void setGroupNo(Long no) {
		setNo(no);
	}
}
