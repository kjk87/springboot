package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Fan")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fan extends User {

	private static final long serialVersionUID = 5771409384600852922L;
	private Long fanNo;
	private Boolean block;
	private Integer buyCount;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastBuyDatetime;
}
