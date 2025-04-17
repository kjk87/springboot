package kr.co.pplus.store.pg.daou.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
    public static boolean isEmptyString(String InStr) {
        return InStr == null || "".equals(InStr);
    }

    public static String checkNull(String InStr) {
        return isEmptyString(InStr) ? "" : Injection(InStr);
    }

    public static String checkNull(String InStr, String InVal) {
        return isEmptyString(InStr) ? InVal : Injection(InStr);
    }

    public static Date getDate() {
        return new Date();
    }

    public static String getFormatDate() {
        return getDate("yyyy-MM-dd HH:mm:ss", getDate());
    }

    public static String getDate(String Dtype, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(Dtype);
        String m_Date = formatter.format(date);
        formatter = null;
        return m_Date;
    }

    public static String addzero(int InStr, int paramLeng) {
        String result = Integer.toString(InStr);
        return addzero(result, paramLeng);
    }

    public static String addzero(String InStr, int paramLeng) {
        int len = paramLeng - InStr.length();
        if (InStr.length() >= paramLeng) {
            return "".concat(String.valueOf(String.valueOf(InStr)));
        } else {
            for(int i = 0; i < len; ++i) {
                InStr = "0".concat(String.valueOf(String.valueOf(InStr)));
            }

            return InStr;
        }
    }

    public static String getMoney(long InStr) {
        return NumberFormat.getInstance().format(InStr);
    }

    public static String getMoney(String InStr) {
        return getMoney(Long.parseLong(InStr));
    }

    public static String MonthForm(String paramDate, String paramDeli) {
        return paramDate.length() != 6 ? paramDate : String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(paramDate.substring(0, 4))))).append(paramDeli).append(paramDate.substring(4))));
    }

    public static String dateForm(String paramDate, String paramDeli) {
        return paramDate.length() != 8 ? paramDate : String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(paramDate.substring(0, 4))))).append(paramDeli).append(paramDate.substring(4, 6)).append(paramDeli).append(paramDate.substring(6))));
    }

    public static String dateDefault(String paramDate) {
        return dateForm(paramDate, "-");
    }

    public static String dateAndTimeForm(String paramDate, String paramDeli1, String paramDeli2, String paramDeli3) {
        return paramDate.length() != 12 ? paramDate : String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(paramDate.substring(0, 4))))).append(paramDeli1).append(paramDate.substring(4, 6)).append(paramDeli1).append(paramDate.substring(6, 8)).append(paramDeli2).append(paramDate.substring(8, 10)).append(paramDeli3).append(paramDate.substring(10))));
    }

    public static String dateAndTimeForm(String paramDate) {
        if (paramDate.length() == 14) {
            return String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(paramDate.substring(0, 4))))).append("-").append(paramDate.substring(4, 6)).append("-").append(paramDate.substring(6, 8)).append("<BR>").append(paramDate.substring(8, 10)).append(":").append(paramDate.substring(10, 12)).append(":").append(paramDate.substring(12))));
        } else if (paramDate.length() == 12) {
            return String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(paramDate.substring(0, 4))))).append("-").append(paramDate.substring(4, 6)).append("-").append(paramDate.substring(6, 8)).append("<BR>").append(paramDate.substring(8, 10)).append(":").append(paramDate.substring(10))));
        } else {
            return paramDate.length() == 8 ? dateForm(paramDate, "-") : paramDate;
        }
    }

    public static String linkDateForm(String paramDate) {
        if (paramDate.length() == 14) {
            paramDate = paramDate.substring(0, 12);
        }

        return paramDate.length() == 12 ? String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(paramDate.substring(0, 4))))).append("년 ").append(paramDate.substring(4, 6)).append("월 ").append(paramDate.substring(6, 8)).append("일 ").append(paramDate.substring(8, 10)).append("시 ").append(paramDate.substring(10)).append("분"))) : paramDate;
    }

    public static String regNoForm(String InRegNo) {
        return InRegNo.length() != 10 ? InRegNo : String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(InRegNo.substring(0, 3))))).append("-").append(InRegNo.substring(3, 5)).append("-").append(InRegNo.substring(5))));
    }

    public static String titleFormat(String InTitle, int InLen) {
        try {
            if (InTitle.trim().length() < InLen) {
                return InTitle;
            } else {
                InTitle = String.valueOf(String.valueOf(InTitle.substring(0, InLen))).concat("...");
                return InTitle;
            }
        } catch (Exception var4) {
            return InTitle;
        }
    }


    public static String SpaceInsert(String InString, String InSpace, int InLen, String InOrder) {
        try {
            if (InString.getBytes().length > InLen) {
                InString.substring(0, InLen - 1);
            } else if (InString.getBytes().length < InLen) {
                for(int m_StringLen = InString.getBytes().length; m_StringLen < InLen; ++m_StringLen) {
                    if (InOrder.equals("R")) {
                        InString = InSpace.concat(InString);
                    } else if (InOrder.equals("L")) {
                        InString = InString.concat(InSpace);
                    }
                }
            }
        } catch (Exception var5) {
        }

        return InString;
    }


    public static String Injection(String InParam) {
        InParam = InParam.replaceAll("--", "");
        InParam = InParam.replaceAll("exec ", "e xec");
        return InParam;
    }

    public static String bytesSubStr(String InStr, int InStartIdx, int InEndIdx) {
        return new String(InStr.getBytes(), InStartIdx, InEndIdx);
    }

    public static String shorten(String InStr, int InLen) {
        if (InStr == null) {
            return InStr;
        } else if (InStr.length() <= InLen) {
            return InStr;
        } else {
            InStr = String.valueOf(String.valueOf(InStr.substring(0, InLen))).concat("...");
            return InStr;
        }
    }

    public static String enterTobr(String InStr) {
        if (InStr == null) {
            return InStr;
        } else {
            InStr = InStr.replaceAll("\r\n", "<br>");
            return InStr;
        }
    }
}
