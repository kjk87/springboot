package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Customer;
import kr.co.pplus.store.type.model.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("CustomerListDto")
public class CustomerListDto extends AbstractModel {
	private Page page;
	private List<Customer> customerList;
	
}
