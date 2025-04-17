package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("ActionForAsync")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionForAsync extends AbstractModel {
	
	private static final long serialVersionUID = -1013398962056761806L;
	
	
	/**
	 * bindFriendByContact : 주소록 전화번호 변경으로 인한 친구 관계 조정
	 * bindFriendByContactAll : 주소록 전체 변경으로 인한 친구 관계 조정
	 */
	private String action;
	private NoOnlyKey target;
}
