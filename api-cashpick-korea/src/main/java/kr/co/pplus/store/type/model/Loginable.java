package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Loginable")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loginable extends NoOnlyKey {

	private static final long serialVersionUID = 5292025094269845505L;
	
	private String name;
	private String loginId;
	private String password;
	
	public Loginable() {
		
	}
	
	public Loginable(Long no) {
		setNo(no);
	}


}
