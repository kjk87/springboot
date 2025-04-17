package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BolHistoryTarget")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BolHistoryTarget extends User {
	private static final long serialVersionUID = 9093588919046036252L;
	
	private Boolean received;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date receiveDate;
	private Long amount;
	private BolHistory history;
}
