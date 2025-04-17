package kr.co.pplus.store.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import kr.co.pplus.store.util.SecureUtil;

public class MobileTypeHandler implements TypeHandler<String> {
	private String convert(String value) {
		if (value != null){
			// ToDo
			//MGK_IMSI : leave_xxx 인 경우 복호화 안함 : 저장 시 암호화 해야함
			if( value.startsWith("leave") )
				return value ;
			else
				return SecureUtil.decryptMobileNumber(value);
		}
		else {
			return null;
		}
	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		return convert(rs.getString(columnName));
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		return convert(rs.getString(columnIndex));
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return convert(cs.getString(columnIndex));
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter,
			JdbcType arg3) throws SQLException {
		if (parameter != null) {

			ps.setString(i, SecureUtil.encryptMobileNumber(parameter));
		}
		else {
			ps.setString(i, null);
		}
	}

	public static void main(String[] args){

		System.out.println(SecureUtil.encryptMobileNumber("01048771673")) ;
	}
}
