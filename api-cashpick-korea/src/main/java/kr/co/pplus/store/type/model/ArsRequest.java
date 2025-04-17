package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("ArsRequest")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArsRequest extends NoOnlyKey {
	private Map<String, Object> properties;
	private String result;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	
	public Integer getNewNo() {
		return getNo().intValue() % 100000;
	}
}
