package kr.co.pplus.store.exception;

import java.util.Map;

import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.util.StoreUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NeedAgreeCompulsoryTerms extends ResultCodeException {
	public NeedAgreeCompulsoryTerms() {
		super(Const.E_NEED_COMPULSORY_TERMS);
	}
	
	public NeedAgreeCompulsoryTerms(Object...args) {
		super(Const.E_NEED_COMPULSORY_TERMS);
		setExtra(StoreUtil.convertStringKeyMap(args));
	}
}
