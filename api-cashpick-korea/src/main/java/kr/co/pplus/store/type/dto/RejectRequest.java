package kr.co.pplus.store.type.dto;

import java.util.Date;

import kr.co.pplus.store.type.model.AbstractModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("RejectRequest")
public class RejectRequest extends AbstractModel {
	private String rejectnumber;
	private String cid;
	private String rejectdate;
}
