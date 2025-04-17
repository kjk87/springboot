package kr.co.pplus.store.api.jpa.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pplus.store.util.SecureUtil;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.text.SimpleDateFormat;
import java.util.Date;

@Converter(autoApply = true)
public class JpaConverterMobileNumber implements AttributeConverter<String, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToEntityAttribute(String s) {
        return SecureUtil.decryptMobileNumber(s);
    }

    @Override
    public String convertToDatabaseColumn(String s) {
        return SecureUtil.encryptMobileNumber(s);
    }
}