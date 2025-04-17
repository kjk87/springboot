package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.ActiveStatus;
import kr.co.pplus.store.type.model.code.Platform;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AppVrs")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppVrs extends AbstractModel {

	private static final long serialVersionUID = 1278632274913362704L;
	
	private Long no;
	private Country country;
	private String code;
	private Platform platform;
	private String subject;
	private ActiveStatus status;
	private Boolean compulsory;
	private String version;
	private String buildNumber;
	private String url;
	private String note;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private User modUser;
	
}
