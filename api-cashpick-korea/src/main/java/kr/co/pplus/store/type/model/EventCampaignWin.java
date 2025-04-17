package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventCampaignWin")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventCampaignWin extends AbstractModel implements Serializable {


	private Long seqNo;
	private Long eventCampaignSeqNo;
	private Long memberSeqNo;
	private Integer winCount;
	private Date lastWinDatetime;
}
