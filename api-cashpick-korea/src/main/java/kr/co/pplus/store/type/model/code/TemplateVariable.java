package kr.co.pplus.store.type.model.code;

public enum TemplateVariable {
	VERIFICATION_NUMBER,
	CREATE_TIME;
	
	public String getVariable() {
		return "\\[\\[!" + name() + "!\\]\\]";
	}
}
