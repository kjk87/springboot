package kr.co.pplus.store.mybatis.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeTypeHandler implements TypeHandler<String> {
	private static final Calendar CALENDAR = Calendar.getInstance();
	private String convert(Timestamp value) {
		if (value == null) {
			return null;
		}
		else {
			try {
				Date date = new Date(value.getTime());
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss") ;
				return sdf.format(date) ;
			} catch (Exception ex) {
				// logger.error("Unexpected IOEx decoding json from database: " + dbData);
				return null;
			}
		}

	}

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
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss") ;
				Date date = sdf.parse(parameter) ;
				ps.setTimestamp(i, new Timestamp(date.getTime()));
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
