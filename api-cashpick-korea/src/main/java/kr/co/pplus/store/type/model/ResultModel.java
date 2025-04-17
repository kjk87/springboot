package kr.co.pplus.store.type.model;

public interface ResultModel extends Model {
	public String getErrorCode();
	public String getErrorMessage();
	public boolean isSuccess();
}
