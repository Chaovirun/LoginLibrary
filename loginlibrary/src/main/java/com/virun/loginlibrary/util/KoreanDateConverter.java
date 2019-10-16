package com.virun.loginlibrary.util;

public class KoreanDateConverter {

    private static String year,month,day;

    public static String findDayOfWeek(String strDate) {
        year = strDate.substring(0, 4);
        month = strDate.substring(4, 6);
        day = strDate.substring(6, 8);

        String dayOfWeek[] = {
                "(일)",
                "(월)",
                "(화)",
                "(수)",
                "(목)",
                "(금)",
                "(토)"
        };
        String result="";

        int lastTwoDigitofYear = Integer.parseInt(year.substring(2,4));
        int step1 = lastTwoDigitofYear / 4;
        int step2 = lastTwoDigitofYear + step1; // 31
        int codeOfYear = step2 - (step2 / 7 * 7); // 31 - 28

        int codeOfMonths[] = {
                0,3,3,6,1,4,6,3,5,0,3,5
        };
        int codeOfMonth = 0;
        for (int i=0;i<codeOfMonths.length;i++){
            if (i == Integer.parseInt(month)-1){
                codeOfMonth = codeOfMonths[i];
            }
        }

        int codeOfCenturys[]={
                4,2,0,6,4,2,0
                //17,18,19,20,21,22,23
        };
        int codeOfCentury = 0;
        for (int i=0;i<codeOfCenturys.length;i++){
            if (i == Integer.parseInt(year.substring(0,2)) - 17){
                codeOfCentury = codeOfCenturys[i];
            }
        }

        int codeOfDay = codeOfYear + codeOfMonth + codeOfCentury + Integer.parseInt(day);
        int codeOfDay1 = codeOfDay - codeOfDay / 7 * 7;
        for (int i=0;i<7;i++){
            if (codeOfDay1 == i){
                result = dayOfWeek[i];
            }
        }
        String date = month + "월" + day + "일" + result;

        return date;
    }

    public static String getKoreaDate(String strDate){
        year = strDate.substring(0, 4);
        month = strDate.substring(4, 6);
        day = strDate.substring(6, 8);
        String kdate = year + "년" + month + "월" + day + "일";
        return kdate;
    }

}
