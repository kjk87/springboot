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
@Alias("EventWin")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventWin extends AbstractModel {

	private static final long serialVersionUID = 9140312219077584987L;

	private Long id;
	private Event event;
	private Integer winNo;
	private User user;
	private EventGift gift;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date winDate;
	private String impression;
	private Boolean blind;
	private Float amount;
	private Integer giftStatus;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date useDatetime;
	private Integer replyCount;
	private String giftTrId;
	private String giftOrderNo;
	private String giftMobileNumber;
	private Boolean openStatus;
	private String deliveryAddress;
	private String recipient;
	private String deliveryPhone;
	private String deliveryPostCode;
	private Boolean isLotto;
	private String giftType;
	private String giftTitle;
	private Long eventJoinSeqNo;


}
