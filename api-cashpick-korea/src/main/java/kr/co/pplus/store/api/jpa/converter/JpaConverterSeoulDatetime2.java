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
public class JpaConverterSeoulDatetime2 implements AttributeConverter<String, Date> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Date convertToDatabaseColumn(String dateStr) {
        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Seoul")) ;
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


    public static void main(String args[]) {
        String str = AppUtil.localDatetimeNowString() ;

        System.out.println(str) ;
        JpaConverterSeoulDatetime2 converter = new JpaConverterSeoulDatetime2() ;

        try {
            Date zdt = converter.convertToDatabaseColumn(str) ;
            System.out.println(zdt.toString()) ;
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}