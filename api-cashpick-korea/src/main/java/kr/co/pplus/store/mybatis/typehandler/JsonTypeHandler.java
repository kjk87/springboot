package kr.co.pplus.store.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import kr.co.pplus.store.StoreApplication;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.pplus.store.util.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JsonTypeHandler implements TypeHandler<Map<String, Object>> {
	@Autowired
	private static ObjectMapper om;
	private static final TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>(){};

	private static ObjectMapper getObjectMapper() {
		/* MGK_DEL
		if (om == null) {
			om = ApplicationContextProvider.getObjectMapper();
			om =

		}
		*/
		
		return om == null ? new ObjectMapper() : om;
	}
	
	private String convert(Map<String, Object> value) {
		if (value == null)
			return null;
		try {
			return getObjectMapper().writeValueAsString(value);
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private Map<String, Object> convert(String value) {
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
			Map<String, Object> parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, convert(parameter));
	}

	@Override
	public Map<String, Object> getResult(ResultSet rs, String columnName)
			throws SQLException {
		String str = rs.getString(columnName);
		return convert(str);
		
	}

	@Override
	public Map<String, Object> getResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return convert(rs.getString(columnIndex));
	}

	@Override
	public Map<String, Object> getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return convert(cs.getString(columnIndex));
	}
}
