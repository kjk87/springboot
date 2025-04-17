package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdminWork")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminWork extends NoOnlyKey {

	private static final long serialVersionUID = -5928336577694241975L;

	private User actor;
	private String type;
	private NoOnlyKey target;
	private String description;
	private Map<String, Object> properties;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date workDate;
	
	private List<Attachment> attachList;
}
