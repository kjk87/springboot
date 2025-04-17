package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("ReservedBatch")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservedBatch extends AbstractModel {

	private static final long serialVersionUID = 5529748004611076315L;

	private Long no;
	private Country country;
	private String title;
	private String reason;
	

}
