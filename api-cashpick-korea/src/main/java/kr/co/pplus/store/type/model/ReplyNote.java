package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("ReplyNote")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReplyNote extends Note {
	private Note origin;
}
