package kr.co.pplus.store.api.jpa.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime ;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Converter(autoApply = true)
public class JpaConverterDate implements AttributeConverter<String, Date> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Date convertToDatabaseColumn(String dateStr) {
        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()) ;
            ZonedDateTime zdt = ZonedDateTime.parse(dateStr, formatter) ;
            return Date.from(zdt.toInstant()) ;

        } catch (Exception ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public String convertToEntityAttribute(Date dbDate) {
        try {

            ZonedDateTime zdt = dbDate.toInstant().atZone(ZoneId.systemDefault())  ;
            return DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(zdt) ;
        } catch (Exception ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }

}