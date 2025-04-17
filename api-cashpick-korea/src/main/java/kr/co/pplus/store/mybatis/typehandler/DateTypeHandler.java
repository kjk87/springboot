package kr.co.pplus.store.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class DateTypeHandler implements TypeHandler<Date> {
	private Date convert(Timestamp value) {
		if (value == null)
			return null;
		Date date = Date.from(value.toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant());
		return date ;
	}

	@Override
	public Date getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return convert(cs.getTimestamp(columnIndex));
	}

	@Override
	public Date getResult(ResultSet rs, int columnIndex) throws SQLException {
		return convert(rs.getTimestamp(columnIndex));
	}

	@Override
	public Date getResult(ResultSet rs, String columnName) throws SQLException {
		return convert(rs.getTimestamp(columnName));
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, Date parameter,
			JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			ps.setTimestamp(i, null);
			return;
		}
		LocalDateTime localDateTime = parameter.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		ps.setTimestamp(i, Timestamp.valueOf(localDateTime)) ;
	}

	public static void main(String[] argv) {

		System.out.println(new Date()) ;
		LocalDateTime localDateTime = new Date().toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();

		System.out.println(localDateTime) ;
	}
}
