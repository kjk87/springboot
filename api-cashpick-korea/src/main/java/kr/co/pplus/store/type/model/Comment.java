package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Comment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment extends NoOnlyKey {

	private static final long serialVersionUID = -5776353310789748341L;

	private String comment;
	private Boolean blind;
	private Long group;
	private Integer depth;
	private Integer priority;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private Boolean deleted;
	private Comment parent;
	private User author;
	private Article post;
	private Map<String, Object> properties;
	
	public void setCommentNo(Long no) {
		setNo(no);
	}
}
