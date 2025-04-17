package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MobileGiftImage")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileGiftImage extends AbstractModel {
	
	private static final long serialVersionUID = -4779950561093488591L;

	private MobileGift mobileGift;
	private Integer no;
	private String code;
	private String path;
	private String width;
	private String height;
	private String memo;
}
