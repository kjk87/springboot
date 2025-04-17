package kr.co.pplus.store.api.jpa.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Converter(autoApply = true)
public class JpaConverterBoolean implements AttributeConverter<Boolean, Integer> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Integer convertToDatabaseColumn(Boolean flag) {
        try {
            if( flag != null && flag == true ) {
                return 1 ;
            } else {
                return 0 ;
            }


        } catch (Exception ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public Boolean convertToEntityAttribute(Integer value) {
        try {

            if( value != null && value == 1 ) {
                return true ;
            } else {
                return false ;
            }
        } catch (Exception ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }

}