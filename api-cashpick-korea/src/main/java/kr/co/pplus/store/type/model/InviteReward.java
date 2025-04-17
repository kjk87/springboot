package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("InviteReward")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InviteReward extends AbstractModel implements Serializable {

	private Long seqNo;
	private Long memberSeqNo;
	private String status;//before, request, complete
	private String gift;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date regDatetime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reqDatetime;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date completeDatetime;
}
