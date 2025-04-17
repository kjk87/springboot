package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsOptionTotal {

	private List<GoodsOption> goodsOptionList = null;
	private List<GoodsOptionItem> goodsOptionItemList = null;
	private List<GoodsOptionDetail> goodsOptionDetailList = null;
	
}
