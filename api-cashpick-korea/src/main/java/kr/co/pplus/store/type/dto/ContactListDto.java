package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Contact;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("ContactListDto")
public class ContactListDto extends AbstractModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3925252249452662666L;
	private List<Contact> contactList;
	private Boolean deleteAll;
	private String appType;
}
