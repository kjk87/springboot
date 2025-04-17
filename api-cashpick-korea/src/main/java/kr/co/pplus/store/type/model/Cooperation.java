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
@Alias("Cooperation")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cooperation extends CouponPublisher {
	private static final long serialVersionUID = -3036755083647751359L;

	private Page mainPage;
	private String commerceType;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private Map<String, Object> properties;
	
	public Cooperation() {
		super();
	}

	public Cooperation(Long no) {
		super("cooperation", no);
	}

	public void setCoopNo(Long no) {
		setNo(no);
	}
}
