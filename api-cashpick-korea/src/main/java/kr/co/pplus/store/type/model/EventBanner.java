package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventBanner")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventBanner extends AbstractModel implements Serializable {

	private static final long serialVersionUID = 5825834816123143942L;

	private Event event;
	private Integer bannerNo;
	private String title;
	private String moveType;
	private String moveTargetString;
	private Long moveTargetNumber;
	private Attachment image;
	private Boolean giveReward;
	private Integer pageViewCount;
	private Integer userViewCount;
	private Integer priority;
	
}
