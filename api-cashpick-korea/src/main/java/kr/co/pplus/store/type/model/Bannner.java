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
@Alias("Bannner")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bannner extends NoOnlyKey {
	private String type;
	private String platform;
	private Boolean display;
	private String name;
	private Duration duration;
	private String moveType1;
	private String moveType2;
	private String moveTarget;
	private Integer priority;
	private Long clickCount;
	private Long viewCount;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUesr;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	private User modUser;

}
