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
@Alias("CustomerGroup")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerGroup extends NoOnlyKey {
	private Page page;
	private String name;
	private Boolean defaultGroup;
	private Integer count;
	private Integer priority;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	
	public void setGroupNo(Long no) {
		setNo(no);
	}
}
