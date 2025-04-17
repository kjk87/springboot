package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("LuckyCoupon")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coupon extends NoOnlyKey {

	private static final long serialVersionUID = 3306684157746849775L;

	private User receiver;
	private CouponTemplate template;
	private String code;
	private String method;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date useDate;
	private String status;

	public void setCouponNo(Long no) {
		setNo(no);
	}
}
