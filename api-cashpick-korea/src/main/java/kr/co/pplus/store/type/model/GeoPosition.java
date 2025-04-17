package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("GeoPosition")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeoPosition extends AbstractModel {

	private static final long serialVersionUID = 8493047977265865135L;

	private Double latitude;
	private Double longitude;
}
