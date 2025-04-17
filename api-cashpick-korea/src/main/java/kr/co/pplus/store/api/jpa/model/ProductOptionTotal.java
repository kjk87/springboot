package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductOptionTotal {

	private List<ProductOption> productOptionList = null;
	private List<ProductOptionItem> productOptionItemList = null;
	private List<ProductOptionDetail> productOptionDetailList = null;
	
}
