package kr.co.pplus.store.type.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventResult extends AbstractModel implements Serializable {

	private EventJoin join;
	private EventWin win;
	private String joinDate;
	private Integer joinTerm;
}
