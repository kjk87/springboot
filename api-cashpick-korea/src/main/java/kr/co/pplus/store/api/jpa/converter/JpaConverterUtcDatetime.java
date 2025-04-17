package kr.co.pplus.store.api.jpa.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pplus.store.api.util.AppUtil;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Converter(autoApply = true)
public class JpaConverterUtcDatetime implements AttributeConverter<String, Date> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Date convertToDatabaseColumn(String dateStr) {
        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC")) ;
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
            ZonedDateTime zdt = dbDate.toInstant().atZone(ZoneId.of("UTC"))  ;
            return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(zdt) ;
        } catch (Exception ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }

    public static void main(String argv[]) {
        try {

            System.out.println(AppUtil.localDatetimeNowString()) ;

            final String bookDatetime = AppUtil.utcFromZoneTimeString("Asia/Seoul", "2019-04-26 16:00:00") ;
            System.out.println(bookDatetime);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC")) ;
            final ZonedDateTime zdt2 = ZonedDateTime.parse(bookDatetime, formatter) ;
            System.out.println(Date.from(zdt2.toInstant())) ;
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}