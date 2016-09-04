package com.china.ds.appanalysis.util;

/**
 * Created by Administrator on 2016/9/1 0001.
 */
public class StringUtils {
    public static String toString(Object o) {
        return o != null?o.toString():"";
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
}
