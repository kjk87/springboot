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
@Alias("Promotion")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Promotion extends NoOnlyKey {

	private static final long serialVersionUID = 3305053968211224582L;
	
	private String title;
	private Attachment image;
	private String type;
	private Duration duration;
	private String status;
	private String contents;
	private Map<String, Object> winProperties;
	private Map<String, Object> loseProperties;
	private Integer joinBol;
	private String limitType;
	private Integer limitCount;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;
	private List<PromotionLotsConfig> lotsConfigList;
	
}
