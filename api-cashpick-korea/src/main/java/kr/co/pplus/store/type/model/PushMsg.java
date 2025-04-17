package kr.co.pplus.store.type.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("PushMsg")
@ToString(callSuper = true, includeFieldNames=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PushMsg extends AbstractModel {

	private static final long serialVersionUID = 7825983033219974064L;

	private Long msgNo;
	private String msg_id;
	private String move_type1;
	private String move_type2;
	private String image_path;
	private String image_path1;
	private String subject;
	private String contents;
	private String move_target;
	private String move_target_string;
	private String appType;
	private Integer pushCase;
	private Boolean ios;
	private Boolean aos;
//	private Boolean pcweb ;
//	private Boolean mobileweb ;
//	private Boolean cms ;
//	private Boolean electron ;
	private User sender;
	private List<User> receivers;
	private List<UserApp> targets;
	

}
