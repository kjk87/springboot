package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("CouponPublisher")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponPublisher extends NoOnlyKey {

	private static final long serialVersionUID = -7741305473966402940L;

	private String name;
	private Attachment profileImage;
	private String publisherType;
	
	public CouponPublisher() {
		super();
	}
	
	public CouponPublisher(String publisherType) {
		super();
		setPublisherType(publisherType);
	}
	
	public CouponPublisher(String publisherType, Long no) {
		super(no);
		setPublisherType(publisherType);
	}
}
