package com.legato.music.utils;

import android.text.TextUtils;

public class Conversions {

    public static String getConvertedSocialMediaHandle(String stringToParse) {
        String str = stringToParse;

        if (!TextUtils.isEmpty(str)) {
            // Convert "https://www.instagram.com/example" to "example"
            if (str.startsWith("http")) {
                // Remove trailing / on "https://www.instagram.com/example/
                if (str.endsWith("/")) {
                    str = str.substring(0, str.length() - 1);
                }

                String[] split_str = str.split("/");
                str = split_str[split_str.length - 1];
            }
            // Convert "@HandleName" to "HandleName"
            else if (str.startsWith("@")) {
                str = str.substring(1);
            }
        }
        else {
            str = "";
        }
        return str;
    }
}
