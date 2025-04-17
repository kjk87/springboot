package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("UseAdvertise")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UseAdvertise extends NoOnlyKey {

	private static final long serialVersionUID = -4964901825809367963L;

	private Advertise advertise;
	private User user;
}
