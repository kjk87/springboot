package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageKeyword")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageKeyword extends SearchKeyword {
	private static final long serialVersionUID = -883191912372995309L;
	private Duration duration;
	private String status;
	private Long visitCount;
	private Page page;
}
