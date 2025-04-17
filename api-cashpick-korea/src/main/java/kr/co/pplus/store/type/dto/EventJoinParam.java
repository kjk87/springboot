package kr.co.pplus.store.type.dto;

import kr.co.pplus.store.type.model.EventBanner;
import kr.co.pplus.store.type.model.EventJoin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventJoinParam")
public class EventJoinParam extends EventJoin {
	EventBanner banner;
}
