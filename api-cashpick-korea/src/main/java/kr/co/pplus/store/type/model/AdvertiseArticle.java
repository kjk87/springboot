package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdvertiseArticle")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertiseArticle extends Article {

	private static final long serialVersionUID = 7714788971982348902L;

	private Advertise lastAdvertise;
}
