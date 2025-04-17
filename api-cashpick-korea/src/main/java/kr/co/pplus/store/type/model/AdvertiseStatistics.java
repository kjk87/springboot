package kr.co.pplus.store.type.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdvertiseStatistics")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertiseStatistics extends Advertise {

	private static final long serialVersionUID = -2017095948875839679L;
	
}
