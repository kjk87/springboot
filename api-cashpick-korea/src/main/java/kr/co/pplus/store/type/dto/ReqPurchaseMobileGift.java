package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.MobileGiftPurchase;
import kr.co.pplus.store.type.model.MobileGiftSend;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("ReqPurchaseMobileGift")
public class ReqPurchaseMobileGift extends MobileGiftPurchase {

	private static final long serialVersionUID = -5473570764634485803L;

	private List<MobileGiftSend> targetList;
}
