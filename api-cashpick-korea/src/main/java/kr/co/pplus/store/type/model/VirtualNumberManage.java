package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("VirtualNumberManage")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualNumberManage extends AbstractModel {

	private static final long serialVersionUID = 651837722812807181L;

	private Long seqNo;
	private String type; // page, event, pages, products, link
	private String status; // normal(정상), remove(해제)
	private String virtualNumber;
	private String number;
	private Integer digit; // 자리수
	// pages, products는 list 형태로 테이블 만들어서 저장
	private String groupName;
	private String eventCode; // type.event
	private String url; // type.link
	private Date startDatetime;
	private Date endDatetime;
	private Boolean nbook;
	private String thumbnail;
	private String reason;
	private String removeReason;
	private Date regDatetime;
	private Date removeDatetime;
	private String productType;
	
}
