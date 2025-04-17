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
@Alias("RelationGroup")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationGroup extends AbstractModel {

	private static final long serialVersionUID = 3186113985394646422L;
	
	private Long no;
	private String name;
	private Boolean defaultGroup;
	private String type;
	private Integer priority;
	private Integer count;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;

	public void setGroupNo(Long no) {
		setNo(no);
	}

}
