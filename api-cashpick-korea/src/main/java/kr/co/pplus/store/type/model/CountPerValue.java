package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("CountPerValue")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountPerValue extends AbstractModel {

	private static final long serialVersionUID = 9064011729933501643L;

	private int count;
	private String value;
	private long no;
}
