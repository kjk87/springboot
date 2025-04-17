package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.AttachTargetType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("DefaultImage")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultImage extends AbstractModel {

	private static final long serialVersionUID = -5443395395788670578L;

	private AttachTargetType targetType;
	private Long no;
	private Long targetNo;
	private String filePath;
	private String fileName;
	private String extension;
	private String url;
	private Integer width;
	private Integer height;
	private Integer rotate;
	

}
