package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("OfferResponse")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferResponse extends Article {
	private static final long serialVersionUID = -3047213785502260871L;
	
	private Offer offer;
}
