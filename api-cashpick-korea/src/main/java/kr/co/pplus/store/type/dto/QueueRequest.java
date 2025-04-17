package kr.co.pplus.store.type.dto;

import java.io.Serializable;
import java.util.HashMap;

import kr.co.pplus.store.type.model.code.QueueJobType;
import org.apache.ibatis.type.Alias;

@Alias("QueueRequest")
public class QueueRequest implements Serializable {

	private static final long serialVersionUID = 3845149282636788526L;
	private String jobType;
	private Serializable msg;
	
	private QueueRequest() {
		
	}
	
	public QueueRequest(String jobType) {
		this.jobType = jobType;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public Serializable getMsg() {
		return msg;
	}

	public void setMsg(Serializable msg) {
		this.msg = msg;
	}
	
	
}
