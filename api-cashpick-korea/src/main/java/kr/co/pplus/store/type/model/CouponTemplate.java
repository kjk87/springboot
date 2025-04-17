package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.ActiveStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("CouponTemplate")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponTemplate extends NoOnlyKey {
	
	private static final long serialVersionUID = 5048970863685075077L;

	private String name;
	private String note;
	private String type;
	private Duration duration;
	private Integer downloadLimit;
	private Boolean display;
	private String discountType;
	private Long discount;
	private String condition;
	private String status;
	private Integer downloadCount;
	private Integer giftCount;
	private Integer useCount;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private String publisherType;
	private CouponPublisher publisher;
	private Boolean givePlus;
	private Attachment icon;
	private Map<String, Object> properties;
	
	public void setTemplateNo(Long no) {
		setNo(no);
	}
}
