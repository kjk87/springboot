package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageIntroMovie")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageIntroMovie extends AbstractModel {
	private static final long serialVersionUID = -6810769408259039313L;

	private Page page;
	private Short no;
	private String url;
}
