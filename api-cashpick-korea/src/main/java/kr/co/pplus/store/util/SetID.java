package kr.co.pplus.store.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class SetID {

    private static Random random = new Random();
    private static SimpleDateFormat sf;
    private static StringBuilder sb;

    public static String getID(String type) {

        sf = new SimpleDateFormat("yyMMddmmss");
        sb = new StringBuilder();

        int randNum = random.nextInt(10000)+1000;
        if(randNum > 10000){
            randNum = randNum - 1000;
        }

        String nowDate = sf.format(new Date());
        sb.append(type);
        sb.append(nowDate);
        sb.append(randNum);


        return sb.toString();

    }

}
