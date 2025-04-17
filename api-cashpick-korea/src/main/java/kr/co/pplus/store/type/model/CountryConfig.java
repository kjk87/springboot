package kr.co.pplus.store.type.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("CountryConfig")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountryConfig extends Country {

	private static final long serialVersionUID = -6958185684174269106L;
	private Map<String, Object> properties;
}
