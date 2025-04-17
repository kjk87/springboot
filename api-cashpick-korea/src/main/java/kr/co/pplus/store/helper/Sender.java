package kr.co.pplus.store.helper;

import kr.co.pplus.store.type.model.SysTemplate;

public interface Sender {
	public void send(String target, SysTemplate template) throws Exception;
}
