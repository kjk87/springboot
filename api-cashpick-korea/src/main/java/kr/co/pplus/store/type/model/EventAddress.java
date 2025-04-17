package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("EventAddress")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventAddress extends AbstractModel {

	private static final long serialVersionUID = -2969513234143656510L;

	private Event event;
	private String address;
}
