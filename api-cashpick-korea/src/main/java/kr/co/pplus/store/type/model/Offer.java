package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Offer")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer extends NoOnlyKey {
	private static final long serialVersionUID = 2160776872832248377L;
	
	private User user;
	private PageCategory category;
	private Boolean expired;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expireDate;
	private Integer responseCount;
	private Article requestArticle;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private List<Article> responseList;
	private List<Page> deniedPageList;
}
