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
@Alias("EventBannerPageView")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventBannerPageView extends AbstractModel {

	private static final long serialVersionUID = -3770618984128054127L;

	private EventBanner banner;
	private Integer pageViewNo;
	private User user;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
}
