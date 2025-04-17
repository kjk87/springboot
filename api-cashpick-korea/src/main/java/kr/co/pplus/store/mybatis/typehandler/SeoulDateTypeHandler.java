package kr.co.pplus.store.mybatis.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SeoulDateTypeHandler implements TypeHandler<String> {
	private static final Calendar CALENDAR = Calendar.getInstance(TimeZone.getTimeZone (ZoneId.of("Asia/Seoul")));

	private String convert(Timestamp value) {
		if (value == null) {
			return null;
		}
		else {
			try {
				ZonedDateTime zdt = value.toLocalDateTime().atZone(ZoneId.systemDefault())  ;
				return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul")).format(zdt);

			} catch (Exception ex) {
				// logger.error("Unexpected IOEx decoding json from database: " + dbData);
				return null;
			}
		}

	}

	/*
	static public String localToUtc(String seoul){

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
		ZonedDateTime zdt = ZonedDateTime.parse(seoul, formatter);
		return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(zdt.toInstant().atZone(ZoneId.of("UTC"))) ;
	}
	*/


	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {

		return convert(cs.getTimestamp(columnIndex, CALENDAR));
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {

		return convert(rs.getTimestamp(columnIndex, CALENDAR));
	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {

		return convert(rs.getTimestamp(columnName, CALENDAR));
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter,
			JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
				ZonedDateTime zdt = ZonedDateTime.parse(parameter, formatter);
				ps.setTimestamp(i, Timestamp.from(zdt.withZoneSameInstant(ZoneId.systemDefault()).toInstant())) ;
			}
			catch(Exception e){
				ps.setTimestamp(i, null);
			}
		}
		else {
			ps.setTimestamp(i, null);
		}
	}
}
