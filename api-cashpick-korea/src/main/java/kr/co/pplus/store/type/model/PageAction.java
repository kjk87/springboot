package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageAction")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageAction extends AbstractModel {

	private static final long serialVersionUID = -8086544035500635969L;
	
	private User user;
	private Page page;
	private Article review;
	private Boolean recvReviewBol;
	private Integer useCount;
}
