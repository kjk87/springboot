package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Customer")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Customer extends NoOnlyKey {
	private Page page;
	private String name;
	private String mobile;
	private String inputType;
	private String status;
	private String marketingConfig;
	private User target;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	
	public void setCustNo(Long no) {
		setNo(no);
	}
}
