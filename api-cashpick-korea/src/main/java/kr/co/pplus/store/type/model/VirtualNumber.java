package kr.co.pplus.store.type.model;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.type.model.code.ActionSource;
import kr.co.pplus.store.type.model.code.OpenBound;
import kr.co.pplus.store.type.model.code.VirtualNumberType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("VirtualNumber")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualNumber extends AbstractModel {

	private static final long serialVersionUID = 651837722812807181L;
	
	private String number;
	private String type;
	private Boolean reserved;
	private String openBound;
	private String actSrc;
	private User actor;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date actDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reservedDate;
	private String note;
	private String reservedTitle;
	private String reservedReason;
	private String reservedDesc;
	private Boolean deleted;
	private Map<String, Object> properties;
	
}
