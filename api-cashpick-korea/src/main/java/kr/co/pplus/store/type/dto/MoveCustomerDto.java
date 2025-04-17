package kr.co.pplus.store.type.dto;

import kr.co.pplus.store.type.model.AbstractModel;
import kr.co.pplus.store.type.model.Customer;
import kr.co.pplus.store.type.model.CustomerGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("MoveCustomerDto")
public class MoveCustomerDto extends AbstractModel {
	private Customer customer;
	private CustomerGroup src;
	private CustomerGroup dest;
}
