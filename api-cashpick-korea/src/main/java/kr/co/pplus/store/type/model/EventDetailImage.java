package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventDetailImage")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDetailImage extends AbstractModel implements Serializable {

	private Long seqNo;
	private Long eventSeqNo;
	private String image;
}
