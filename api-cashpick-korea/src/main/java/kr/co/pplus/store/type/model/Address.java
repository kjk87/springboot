package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Address")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address extends AbstractModel {

	private static final long serialVersionUID = 4095353096876229143L;

	private String zipCode;
	private String roadBase;
	private String roadDetail;
	private String parcelBase;
	private String parcelDetail;
	
}
