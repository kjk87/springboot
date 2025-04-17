package kr.co.pplus.store.api.util;

import javax.servlet.http.HttpServletRequest;

import kr.co.pplus.store.type.model.User;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;


public class AppUtil {
    private static final Logger logger = LoggerFactory.getLogger(AppUtil.class);
    public static Map<String, String> ConverObjectToMap(Object obj) {
        try {
            //Field[] fields = obj.getClass().getFields();
            // private field는 나오지 않음.
            Field[] fields = obj.getClass().getDeclaredFields();
            Map<String, String> resultMap = new HashMap<String, String>();
            for (int i = 0; i <= fields.length - 1; i++) {
                fields[i].setAccessible(true);
                resultMap.put(fields[i].getName(), (String) fields[i].get(obj));
            }
            return resultMap;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HttpGet getGet(String url) throws Exception {
        HttpGet get = new HttpGet(url);
        URI uri = new URIBuilder(get.getURI()).build();
        get.setURI(uri);
        return get;
    }

    public static HttpGet getGet(String url, List<NameValuePair> nameValuePairList) throws Exception {
        HttpGet get = new HttpGet(url);
        get.setHeader("Accept", "application/json; charset=UTF-8");
        get.setHeader("Content-Type", "application/json; charset=UTF-8");
        get.setHeader("Accept-Charset", "UTF-8");
        URI uri = new URIBuilder(get.getURI()).addParameters(nameValuePairList).build();
        get.setURI(uri);
        return get;
    }

    public static HttpPost getPost(String url, StringEntity entity) {
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json; charset=UTF-8");
        post.setHeader("Content-Type", "application/json; charset=UTF-8");
        post.setHeader("Accept-Charset", "UTF-8");
        post.setEntity(entity);
        return post;
    }

    public static HttpPost getPost(String url, List<NameValuePair> nameValuePairList) {
        String params = URLEncodedUtils.format(nameValuePairList, "utf-8");
        params = params.replace("%EF%BB%BF", "");

        HttpPost post = new HttpPost(url+ "?" + params);
        post.setHeader("Accept", "application/json; charset=UTF-8");
        post.setHeader("Content-Type", "application/json; charset=UTF-8");
        post.setHeader("Accept-Charset", "UTF-8");
        return post;
    }

    public static HttpPost getPostFormData(String url, Object data) throws Exception {
        List<NameValuePair> nameValuePairs = AppUtil.getNameValuePairs(data);
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json; charset=UTF-8");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Accept-Charset", "UTF-8");
        post.setEntity(entity);
        return post;
    }

    public static HttpPost getPostFormUrlEncoded(String url, List<NameValuePair> nameValuePairs) throws Exception {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json; charset=UTF-8");
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Accept-Charset", "UTF-8");
        post.setEntity(entity);
        return post;
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().isEmpty())
            return true;
        else
            return false;
    }

    public static HttpPost getPostWithParams(String url, Object data, Boolean ms949) throws Exception {
        List<NameValuePair> nameValuePairs = AppUtil.getNameValuePairs(data);
        String params = URLEncodedUtils.format(nameValuePairs, "utf-8");
        params = params.replace("%EF%BB%BF", "");
        if (ms949) {
            params = convertUTF8toMS949(params);
        }
        HttpPost post = new HttpPost(url + "?" + params);

        if (ms949) {
            post.setHeader("Accept", "application/json; charset=MS949");
            post.setHeader("Content-Type", "application/x-www-form-urlencode");
            post.setHeader("Accept-Charset", "MS949");
        } else {
            post.setHeader("Accept", "application/json; charset=UTF-8");
            post.setHeader("Content-Type", "application/x-www-form-urlencode");
            post.setHeader("Accept-Charset", "UTF-8");
        }
        return post;
    }

    public static HttpPost getPostWithParamsEucKr(String url, Object data) throws Exception {
        List<NameValuePair> nameValuePairs = AppUtil.getNameValuePairs(data);
        String params = URLEncodedUtils.format(nameValuePairs, "euc-kr");
        params = params.replace("%EF%BB%BF", "");

        HttpPost post = new HttpPost(url + "?" + params);
        post.setHeader("Content-Type", "application/x-www-form-urlencode");
        return post;
    }

    public static List<NameValuePair> getNameValuePairs(Object obj) throws IllegalArgumentException, IllegalAccessException {
        ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true); // if you want to modify private fields

            if (StringUtils.isNotEmpty((String) field.get(obj))) {
                logger.debug(field.getName() + " : "+(String) field.get(obj));
                NameValuePair nameValuePair = new BasicNameValuePair(field.getName(), (String) field.get(obj));
                list.add(nameValuePair);
            }

        }
        return list;
    }

    public static String convertUTF8toMS949(String utf8Str) throws Exception {

        // ================================
        InputStream is = null;
        Reader reader = null;
        Writer writer = null;
        StringBuffer stringBuffer = new StringBuffer();

        int intRead = 0;

        is = new ByteArrayInputStream(utf8Str.getBytes("UTF-8"));
        Charset inputCharset = Charset.forName("utf-8");
        InputStreamReader isr = new InputStreamReader(is, inputCharset);

        reader = new BufferedReader(isr);

        while ((intRead = reader.read()) > -1) {
            stringBuffer.append((char) intRead);
        }
        reader.close();

        //
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(bos, "MS949");
        writer.write(stringBuffer.toString());
        writer.flush();
        String ms949Str = new String(bos.toByteArray(), "MS949");
        stringBuffer.setLength(0);
        writer.close();
        return ms949Str;
    }

    public static String convertUTF8toMS949(InputStream utf8IS) throws Exception {

        // ================================
        InputStream is = null;
        Reader reader = null;
        Writer writer = null;
        StringBuffer stringBuffer = new StringBuffer();

        int intRead = 0;

        is = utf8IS;
        Charset inputCharset = Charset.forName("utf-8");
        InputStreamReader isr = new InputStreamReader(is, inputCharset);

        reader = new BufferedReader(isr);

        while ((intRead = reader.read()) > -1) {
            stringBuffer.append((char) intRead);
        }
        reader.close();

        //
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(bos, "MS949");
        writer.write(stringBuffer.toString());
        writer.flush();
        String ms949Str = new String(bos.toByteArray(), "MS949");
        stringBuffer.setLength(0);
        writer.close();
        return ms949Str;
    }

    public static String convertMS949toUTF8(String ms949Str) throws Exception {

        // ================================
        InputStream is = null;
        Reader reader = null;
        Writer writer = null;
        StringBuffer stringBuffer = new StringBuffer();

        int intRead = 0;

        is = new ByteArrayInputStream(ms949Str.getBytes("MS949"));
        Charset inputCharset = Charset.forName("MS949");
        InputStreamReader isr = new InputStreamReader(is, inputCharset);

        reader = new BufferedReader(isr);

        while ((intRead = reader.read()) > -1) {
            stringBuffer.append((char) intRead);
        }
        reader.close();

        //
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(bos, "UTF-8");
        writer.write(stringBuffer.toString());
        writer.flush();
        String utf8Str = new String(bos.toByteArray(), "UTF-8");
        stringBuffer.setLength(0);
        writer.close();
        return utf8Str;
    }

    public static String convertMS949toUTF8(InputStream ms949IS) throws Exception {

        // ================================
        InputStream is = null;
        Reader reader = null;
        Writer writer = null;
        StringBuffer stringBuffer = new StringBuffer();

        int intRead = 0;

        is = ms949IS;
        Charset inputCharset = Charset.forName("MS949");
        InputStreamReader isr = new InputStreamReader(is, inputCharset);

        reader = new BufferedReader(isr);

        while ((intRead = reader.read()) > -1) {
            stringBuffer.append((char) intRead);
        }
        reader.close();

        //
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        writer = new OutputStreamWriter(bos, "UTF-8");
        writer.write(stringBuffer.toString());
        writer.flush();
        String utf8Str = new String(bos.toByteArray(), "UTF-8");
        stringBuffer.setLength(0);
        writer.close();
        return utf8Str;
    }

    public static String nvr(String input, String defaultStr) {
        if (input == null)
            return defaultStr;
        else
            return input;
    }

    public static String nvr(String input) {
        if (input == null)
            return "";
        else
            return input;
    }

    public static boolean isGuest(User user) {
        return false;
    }


    public static boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().startsWith("multipart/form-data");
    }

    public static boolean isFormSubmit(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().startsWith(
                "application/x-www-form-urlencoded");
    }

    public static String excetionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString(); // stack trace as a string
    }

    //===== Local Time ======
    public static String localDatetimeNowString() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.now(ZoneId.systemDefault()), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String localDatetimeTodayString() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.now(ZoneId.systemDefault()), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static Date localDatetimeNowDate() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.now(ZoneId.systemDefault()), ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    public static ZonedDateTime localDatetimeNow(long days) {
        return ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()).plusDays(days), LocalTime.now(ZoneId.systemDefault()), ZoneId.systemDefault());
    }

    public static ZonedDateTime localDatetimeNowPlusHour(long hours) {
        return ZonedDateTime.of(LocalDateTime.now(ZoneId.systemDefault()).plus(Duration.ofHours(hours)), ZoneId.systemDefault());

    }

    public static ZonedDateTime localDatetimeNowPlusMin(long minutes) {
        return ZonedDateTime.of(LocalDateTime.now(ZoneId.systemDefault()).plus(Duration.ofMinutes(minutes)), ZoneId.systemDefault());

    }

    public static String localDatetimeNowPlusDayString(int days) {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()).plusDays(days), LocalTime.now(ZoneId.systemDefault()), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static ZonedDateTime localToday() {
        return ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.of(0, 0), ZoneId.systemDefault());
    }

    public static String localTodayString() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.of(0, 0), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String localTodayYYYYMMDD() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.of(0, 0), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String localFromZoneTimeString(String zone, String seoulTimeStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(zone));
        final ZonedDateTime zdt = ZonedDateTime.parse(seoulTimeStr, formatter);
        final ZonedDateTime zdt2 = zdt.toInstant().atZone(ZoneId.systemDefault());
        final String bookDatetimeStr = zdt2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return bookDatetimeStr;
    }

    public static ZonedDateTime localToday(int days) {
        return ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()).plusDays(days), LocalTime.of(0, 0), ZoneId.systemDefault());
    }

    public static String localTodayString(int days) {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.systemDefault()).plusDays(days), LocalTime.of(0, 0), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    //======== UTC =====

    public static ZonedDateTime utcDatetimeNow() {
        return ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.now(ZoneId.of("UTC")), ZoneId.of("UTC"));
    }

    public static String utcDatetimeNowString() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.now(ZoneId.of("UTC")), ZoneId.of("UTC"));
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static ZonedDateTime utcDatetimeNow(int days) {
        return ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")).plusDays(days), LocalTime.now(ZoneId.of("UTC")), ZoneId.of("UTC"));
    }

    public static String utcDatetimeNowString(int days) {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")).plusDays(days), LocalTime.now(ZoneId.of("UTC")), ZoneId.of("UTC"));
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static ZonedDateTime utcToday() {
        return ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.of(0, 0), ZoneId.of("UTC"));
    }

    public static String utcTodayString() {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")), LocalTime.of(0, 0), ZoneId.of("UTC"));
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static String utcFromZoneTimeString(String zone, String seoulTimeStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(zone));
        final ZonedDateTime zdt = ZonedDateTime.parse(seoulTimeStr, formatter);
        final ZonedDateTime zdt2 = zdt.toInstant().atZone(ZoneId.of("UTC"));
        final String bookDatetimeStr = zdt2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        return bookDatetimeStr;
    }

    public static ZonedDateTime utcToday(int days) {
        return ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")).plusDays(days), LocalTime.of(0, 0), ZoneId.of("UTC"));
    }

    public static String utcTodayString(int days) {
        ZonedDateTime zdt = ZonedDateTime.of(LocalDate.now(ZoneId.of("UTC")).plusDays(days), LocalTime.of(0, 0), ZoneId.of("UTC"));
        return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static String getPhoneNumber(String result){

        String regEx = "";
        if(result.startsWith("02")){
            regEx = "(\\d{2})(\\d{3,4})(\\d{4})";
        }else if(result.length() < 12){
            regEx = "(\\d{2,3})(\\d{3,4})(\\d{4})";
        }else if(result.length() == 12){
            regEx = "(\\d{4})(\\d{4})(\\d{4})";
        }else{
            return result;
        }

        if(!Pattern.matches(regEx, result)) return null;

        result = result.replaceAll(regEx, "$1-$2-$3");

        return result;
    }


    public static void main(String argv[]) {
        //System.out.println(utcFromZoneTimeString("Asia/Seoul", "2019-04-26 16:00:00")) ;

//        logger.debug("mobile : "+getPhoneNumber("01038007428"));
    }
}
