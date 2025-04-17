package kr.co.pplus.store.api.jpa.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pplus.store.api.util.AppUtil;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Converter(autoApply = true)
public class JpaConverterSeoulDatetime implements AttributeConverter<String, Date> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Date convertToDatabaseColumn(String dateStr) {
        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul")) ;
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
            ZonedDateTime zdt = dbDate.toInstant().atZone(ZoneId.of("Asia/Seoul"))  ;
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul")).format(zdt) ;
        } catch (Exception ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }

    public static void main(String argv[]) {
        try {
            String dateStr = "2019-07-18 10:00:00" ;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul")) ;
            ZonedDateTime zdt = ZonedDateTime.parse(dateStr, formatter) ;
            System.out.println(zdt) ;
            Date dbDate =  Date.from(zdt.toInstant()) ;
            System.out.println(dbDate) ;
            zdt = dbDate.toInstant().atZone(ZoneId.of("UTC"))  ;
            System.out.println(zdt) ;
            System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zdt.toLocalDateTime())) ;
        } catch (Exception ex) {
           ex.printStackTrace();
        }

        try {
            ZonedDateTime zdt = new Date().toInstant().atZone(ZoneId.of("Asia/Seoul"))  ;
            System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(zdt)) ;
        } catch (Exception ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            ex.printStackTrace();
        }
    }

}