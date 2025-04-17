package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Plus")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Plus extends Page {

	private static final long serialVersionUID = -3924711469775831612L;
	
	private Long plusNo;
	private Boolean block;
	private Boolean pushActivate;
	private Integer buyCount;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastBuyDatetime;
	private Boolean agreement;
	private Boolean plusGiftReceived;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date plusGiftReceivedDatetime;
}
