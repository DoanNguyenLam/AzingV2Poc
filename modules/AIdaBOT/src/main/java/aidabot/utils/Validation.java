package aidabot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    private static final String SCRIPT_PATTERN = "<script\\s+src=\"https://www\\.aidabot\\.io/assets/aidabot\\.min\\.js\"\\s+data-client-id=\"\\w+\"></script>";
    public static boolean validateScript(String script) {
        if (script.isEmpty()) return false;

        Pattern pattern = Pattern.compile(SCRIPT_PATTERN);
        Matcher matcher = pattern.matcher(script);

        return matcher.matches();
    }
}
