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
@Alias("EventBannerUserView")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventBannerUserView extends AbstractModel {

	private static final long serialVersionUID = 3619878612817047369L;

	private EventBanner banner;
	private User user;
	private Boolean giveReward;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
}
