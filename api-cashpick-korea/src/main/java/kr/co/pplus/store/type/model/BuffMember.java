package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("BuffMember")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffMember extends AbstractModel {


	private Long seqNo;

	private Long buffSeqNo;

	private Long memberSeqNo;

	private Boolean isOwner;

	private Double receivedBol;

	private Double dividedBol;

	private Double receivedPoint;

	private Double dividedPoint;

	private Date regDatetime;
	
}
