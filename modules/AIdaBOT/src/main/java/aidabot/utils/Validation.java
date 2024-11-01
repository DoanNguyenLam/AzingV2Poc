package aidabot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static final String SCRIPT_PATTERN = "<script\\s+src=\"https://www\\.aidabot\\.io/assets/aidabot\\.min\\.js\"\\s+data-client-id=\"\\w+\"></script>";
    public static final String URL_PATTERN = "^https:\\/\\/static\\.aidabot\\.io\\/index\\.html\\?id=[a-zA-Z0-9]+&lang=[a-z]{2}$";

    public static boolean validateStr(String str, String patternStr) {
        if (str.isEmpty()) return false;

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);

        return matcher.matches();
    }
}
