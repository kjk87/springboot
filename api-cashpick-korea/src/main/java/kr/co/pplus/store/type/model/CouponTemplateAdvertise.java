package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("CouponTemplateAdvertise")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponTemplateAdvertise extends Advertise {

	private static final long serialVersionUID = 4990566452362052297L;

	private CouponTemplate template;
}
