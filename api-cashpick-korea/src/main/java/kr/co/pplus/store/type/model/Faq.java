package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Faq")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Faq extends NoOnlyKey {

	private static final long serialVersionUID = 5513568564067910092L;

	private FaqGroup group;
	private String status;
	private String subject;
	private String contents;
	private String path;
	private String platform;
	private Integer priority;
	private Date regDate;
	private User regUser;
	private Date modDate;
	private User modUer;
	
}
