package kr.co.pplus.store.mybatis.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UtcDateTypeHandler implements TypeHandler<String> {
	private static final Calendar UTC_CALENDAR = Calendar.getInstance (TimeZone.getTimeZone (ZoneOffset.UTC));
	private String convert(Timestamp value) {
		if (value == null) {
			return null;
		}
		else {
			try {
				ZonedDateTime zdt =value.toLocalDateTime().atZone(ZoneId.systemDefault())  ;
				return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(zdt.withZoneSameInstant(ZoneId.of("UTC"))) ;
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

		return convert(cs.getTimestamp(columnIndex, UTC_CALENDAR));
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {

		return convert(rs.getTimestamp(columnIndex, UTC_CALENDAR));
	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {

		return convert(rs.getTimestamp(columnName, UTC_CALENDAR));
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter,
			JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));
				ZonedDateTime zdt = ZonedDateTime.parse(parameter, formatter);
				ps.setTimestamp(i, Timestamp.from(zdt.withZoneSameInstant(ZoneId.systemDefault()).toInstant()));
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
