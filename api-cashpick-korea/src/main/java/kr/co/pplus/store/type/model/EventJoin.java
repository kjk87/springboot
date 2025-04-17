package kr.co.pplus.store.type.model;

import java.io.Serializable;
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
@Alias("EventJoin")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventJoin extends AbstractModel implements Serializable {

	private static final long serialVersionUID = -3648008871116770450L;

	private Long id;
	private Event event;
	private Integer joinNo;
	private User user;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date joinDate;
	private String winCode;
	private Map<String, Object> properties;
}
