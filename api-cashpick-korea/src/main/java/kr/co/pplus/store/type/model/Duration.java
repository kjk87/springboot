package kr.co.pplus.store.type.model;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper=false)
@Alias("Duration")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Duration extends AbstractModel {

	private static final long serialVersionUID = -4907887616281978313L;

	//@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String start;

	//@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String end;

	public Date getStartDate() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
			ZonedDateTime zdt = ZonedDateTime.parse(this.start, formatter);
			return Date.from(zdt.toInstant()) ;
		}
		catch(Exception e){
			return null ;
		}
	}

	public Date getEndDate() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
			ZonedDateTime zdt = ZonedDateTime.parse(this.end, formatter);
			return Date.from(zdt.toInstant()) ;
		}
		catch(Exception e){
			return null ;
		}
	}

	private String type;
}
