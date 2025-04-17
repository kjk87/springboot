package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdvertiseCouponTemplate")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertiseCouponTemplate extends CouponTemplate {
	
	private static final long serialVersionUID = -4862489214738598561L;
	
	private Advertise lastAdvertise;
}
