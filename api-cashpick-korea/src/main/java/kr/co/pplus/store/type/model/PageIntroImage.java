package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PageIntroImage")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageIntroImage extends Attachment {
	private static final long serialVersionUID = 688299489675159535L;

	private Page page;
	private Short priority;
}
