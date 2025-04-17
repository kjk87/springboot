package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("FanGroup")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FanGroup extends RelationGroup {

	private static final long serialVersionUID = 6472261512533109161L;

	private Page page;
	
}
