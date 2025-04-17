package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.CouponTemplate;
import kr.co.pplus.store.type.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("GiveCoupon")
public class GiveCoupon extends CouponTemplate {

	private static final long serialVersionUID = 7118470276180786953L;
	
	private List<User> receiverList;
	
}
