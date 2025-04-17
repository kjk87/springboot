package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventCampaign")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventCampaign extends AbstractModel implements Serializable {

	private Long seqNo;
	private String code;
	private String title;
	private String status;
	private Date regDatetime;
	private String register;
	private Long agentSeqNo;
	private String joinLimit;//none, campaign
	private Integer joinCount;
}
