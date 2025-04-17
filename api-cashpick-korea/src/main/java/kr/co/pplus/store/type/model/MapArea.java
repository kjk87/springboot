package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MapArea")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MapArea extends AbstractModel {

	private static final long serialVersionUID = 4217943396784672723L;

	private Double top;
	private Double left;
	private Double bottom;
	private Double right;
}
