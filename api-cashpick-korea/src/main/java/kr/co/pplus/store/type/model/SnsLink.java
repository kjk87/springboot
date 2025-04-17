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
@Alias("SnsLink")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SnsLink extends AbstractModel {

	private static final long serialVersionUID = 3882703115912408254L;
	
	private Integer no;
	private Page page;
	private String type;
	private Boolean linkage;
	private String url;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date modDate;
	
}
