package kr.co.pplus.store.type.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("InAppBolHistory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InAppBolHistory extends BolHistory {
	private Map<String, Object> paymentProperties;
	private Map paymentResult;

}
