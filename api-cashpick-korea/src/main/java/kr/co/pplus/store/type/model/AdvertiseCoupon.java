package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdvertiseCoupon")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertiseCoupon extends Coupon {

	private static final long serialVersionUID = -6526742329753666796L;

	private Advertise advertise;
}
