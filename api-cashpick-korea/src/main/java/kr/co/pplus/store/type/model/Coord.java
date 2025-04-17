package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Coord")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coord extends AbstractModel {
	private Double x;
	private Double y;
}
