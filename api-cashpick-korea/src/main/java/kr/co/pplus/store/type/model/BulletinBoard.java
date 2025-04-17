package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BulletinBoard")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulletinBoard extends NoOnlyKey {
	private static final long serialVersionUID = 3842086572967731042L;

	private String type;
	private String name;
	private String listFormat;
	private String viewFormat;
	private Map<String, Object> properties;
	private Date regDate;

	private Page page;
	
	public void setBoardNo(Long no) {
		setNo(no);
	}
}
