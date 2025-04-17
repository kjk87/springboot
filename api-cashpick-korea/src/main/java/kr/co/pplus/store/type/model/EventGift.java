package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventGift")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventGift extends AbstractModel {

	private static final long serialVersionUID = -908203737243450448L;

	private Long seqNo;
	private Event event;
	private Long giftNo;
	private String type;
	private String title;
	private Integer totalCount;
	private Integer remainCount;
	private String alert;
	private Double lotPercent;
	private Long price;
	private String winOrder;
	private Attachment image;
	private String betaCode;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expireDate;
	private String timeType;//all, time
	private String dayType;//all, day
	private String startTime;
	private String endTime;
	private String days;
	private Integer reviewPoint;
	private Boolean reviewPresent;
	private Integer manualChoice;
	private String giftLink;
	private Boolean autoSend;
	private Long giftishowSeqNo;
	private String giftImageUrl;
	private Boolean best;
	private Boolean temp;
	private Boolean delivery = null;
	private Boolean saveBuff;

	private List<EventWin> eventWinList;

}
