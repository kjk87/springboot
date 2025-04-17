package kr.co.pplus.store.type.dto;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * 사용자 기반의 특정 기능 처리시 인자값
 * @author sykim
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Alias("UserAction")
public class UserAction extends AbstractModel {
	private String actionType;
	private User user;
}
