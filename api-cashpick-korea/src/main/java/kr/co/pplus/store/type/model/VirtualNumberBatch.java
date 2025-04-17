package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.VirtualNumberType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("VirtualNumberBatch")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualNumberBatch extends AbstractModel {
	
	private static final long serialVersionUID = 3284845654722916091L;
	
	private Long no;
	private Country country;
	private VirtualNumberType type;
	private String note;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDate;
	private User regUser;
	

}
