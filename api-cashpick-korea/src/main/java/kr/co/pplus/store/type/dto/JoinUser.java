package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.App;
import kr.co.pplus.store.type.model.Terms;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.VirtualNumber;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("JoinUser")
public class JoinUser extends User {
	private static final long serialVersionUID = 5731994602829565593L;
	private App app;
	private VirtualNumber number;
	private List<Terms> termsList;
	private Boolean encrypted;
	private Boolean isKakao;

	public JoinUser() {
		
	}
	
	public JoinUser(Long no) {
		super(no);
	}
}
