package kr.co.pplus.store.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/*
public class BooleanTypeHandler extends BaseTypeHandler<Boolean> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, convert(parameter));
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return convert(rs.getString(columnName));
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return convert(rs.getString(columnIndex));
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return convert(cs.getString(columnIndex));
	}

	private String convert(Boolean b) {
		return b == null ? null : (b ? "Y" : "N") ;
	}

	private Boolean convert(String s) {
		if( s == null )
			return null ;
		else
			return s.equals("Y");
	}

}
*/



public class BooleanTypeHandler implements TypeHandler<Boolean> {
	protected static Boolean validation(String value) {
		if (value != null) {
			if (value.trim().equalsIgnoreCase("Y"))
				return true;
			else
				return false;
		}
		return null;
	}
	
	protected static String getStringValue(Boolean value) {
		if (value != null) {
			if (value == true)
				return "Y";
			else
				return "N";
		}
		return null;
	}
	
	private static Boolean convert(String value) {
		Boolean v = validation(value);
		if (v == null || value == null) 
			return null;
		else if (validation(value) == true)
			return new Boolean(true);
		else
			return new Boolean(false);
	}
	
	@Override
	public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
		return convert(rs.getString(columnName));
	}

	@Override
	public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
		return convert(rs.getString(columnIndex));
	}

	@Override
	public Boolean getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return convert(cs.getString(columnIndex));
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, Boolean parameter,
			JdbcType jdbcType) throws SQLException {
		if (JdbcType.CHAR == jdbcType || JdbcType.VARCHAR == jdbcType)
			ps.setString(i, getStringValue(parameter));
		else
			ps.setString(i, null);
	}
}