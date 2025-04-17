package kr.co.pplus.store.type.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("AdvertiseWinner")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdvertiseWinner extends User {

	private static final long serialVersionUID = 8114957621703669229L;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date winDate;
	private Advertise advertise;
}
