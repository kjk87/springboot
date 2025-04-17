package kr.co.pplus.store.api.jpa.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Converter(autoApply = true)
public class JpaConverterTime implements AttributeConverter<String, Date> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Date convertToDatabaseColumn(String myTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date dbTime = sdf.parse(myTime) ;
            return dbTime ;
        } catch (Exception ex) {
            return null;
            // or throw an error
        }
    }

    @Override
    public String convertToEntityAttribute(Date dbTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String myTime = sdf.format(dbTime);
            return myTime ;
        } catch (Exception ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            return null;
        }
    }

    public static void main(String argv[]){

        String myTime = "17:30";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf.parse(myTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String formattedTime = sdf.format(date);

        System.out.println(formattedTime);
    }

}