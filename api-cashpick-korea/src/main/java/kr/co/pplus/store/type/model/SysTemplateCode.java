package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("SysTemplateCode")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysTemplateCode extends AbstractModel {
	private static final long serialVersionUID = -8440205198525350897L;

	private String code;
	private Integer depth;
	private String name;
}
