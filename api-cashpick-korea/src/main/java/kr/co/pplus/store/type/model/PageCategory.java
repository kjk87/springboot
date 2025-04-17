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
@Alias("PageCategory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageCategory extends NoOnlyKey {

	private static final long serialVersionUID = 713961021810390396L;
	
	private String code;
	private String type;
	private PageCategory parent;
	private Integer depth;
	private String name;
	private Integer priority;
	private ActiveStatus status;
	private String iconImageUrl;
	private String pageImageUrl;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private User modUser;
	private String uuid ; // 이미지 uuid
	private Boolean thema ;

	public void setCategoryNo(Long no) {
		super.setNo(no);
	}
}
