package kr.co.pplus.store.api.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Test {

    public static void main(String[] argv){
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.now(ZoneId.of("UTC")), ZoneId.of("UTC"));
        String dateStr =  zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        System.out.println(dateStr) ;
    }
}
