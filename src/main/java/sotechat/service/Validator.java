package sotechat.service;

import org.apache.commons.lang.StringEscapeUtils;
//import org.owasp.esapi.ESAPI;

public class Validator {

    private final static String allowed =
            "0123456789abcdefghijklmnopqrstuvxyzåäö()*!?\"\'.,:+-=%#@_";

    public final static String sanitize(String input) {
        String output = "";

        for (int i = 0; i < input.length(); i++) {
            if (allowed.indexOf(input.charAt(i)) >= 0) {
                output += input.charAt(i);
            }
        }
        return escape(output);
    }


    public static String escape(String input) {
        return StringEscapeUtils.escapeJava(input);
    }

    public static String unescape(String input) {
        return StringEscapeUtils.unescapeJava(input);
    }
/**
 public static String escapeForSql(String input) {


 }

 public static String unescapeFromSql(String input) {

 }
 */
}