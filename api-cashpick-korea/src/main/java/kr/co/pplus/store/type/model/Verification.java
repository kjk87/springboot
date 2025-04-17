package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Verification")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Verification extends AbstractModel {
	private static final long serialVersionUID = 6292024766648858283L;
	
	private String token;
	private String name;
	private String number;
	private String type;
	private String media;
	private String mobile;
	private String email;
	private String loginId;
	private String password;
	private String authCode;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;

	private String appType;
	
}
