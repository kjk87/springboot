package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.PostType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Post는 페이지에 종속적이다.
 * @author sykim
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Article")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article extends NoOnlyKey {
	
	private static final long serialVersionUID = 279420365923357691L;

	private String subject;
	private BulletinBoard board;
	private User author;
	private String contents;
	private Integer priority;
	private Long viewCount;
	private Integer commentCount;
	private Boolean blind;
	private String type;  //page_pr,page_review, inquiry,suggest,cooperation,offer,system_common, member
	private String articleUrl;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private List<Attachment> attachList;

	private String appType;
	
	public void setPostNo(Long no) {
		setArticleNo(no);
	}
	
	public void setArticleNo(Long no) {
		setNo(no);
	}
	
	public void setPostType(String type) {
		setArticleType(type);;
	}
	
	public void setArticleType(String type) {
		setType(type);
	}
	
}
