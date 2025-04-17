package kr.co.pplus.store.type.dto;

import java.util.List;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Customer;
import kr.co.pplus.store.type.model.CustomerGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MoveCustomerListDto")
public class MoveCustomerListDto extends AbstractModel {
	private CustomerGroup src;
	private CustomerGroup dest;
	private CustomerGroup group;
	private List<Customer> customerList;
}
