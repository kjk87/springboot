package kr.co.pplus.store.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.pplus.store.util.ApplicationContextProvider;

public class ListJsonTypeHandler implements TypeHandler<List<Object>> {
	private static ObjectMapper om;
	private static final TypeReference<List<Object>> typeRef = new TypeReference<List<Object>>(){};

	private static ObjectMapper getObjectMapper() {
		if (om == null) {
			om = ApplicationContextProvider.getObjectMapper();
		}
		
		return om == null ? new ObjectMapper() : om;
	}
	
	private String convert(List<Object> value) {
		if (value == null)
			return null;
		try {
			return getObjectMapper().writeValueAsString(value);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private List<Object> convert(String value) {
		if (value == null)
			return null;
		try {
			return getObjectMapper().readValue(value, typeRef);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setParameter(PreparedStatement ps, int i,
			List<Object> parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, convert(parameter));
	}

	@Override
	public List<Object> getResult(ResultSet rs, String columnName)
			throws SQLException {
		String str = rs.getString(columnName);
		return convert(str);
		
	}

	@Override
	public List<Object> getResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return convert(rs.getString(columnIndex));
	}

	@Override
	public List<Object> getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return convert(cs.getString(columnIndex));
	}
}
