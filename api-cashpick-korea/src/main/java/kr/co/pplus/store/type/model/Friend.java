package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Friend")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Friend extends Contact {

	private static final long serialVersionUID = -6773010334964932824L;

	private User friend;
	private Boolean existsPageInContact;
}
