package sotechat.service;

import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.esapi.ESAPI;

/**
 * Created by varkoi on 16.6.2016.
 */
public class Validator {

    private final static String allowed =
            "0123456789abcdefghijklmnopqrstuvxyzåäö()*!?\"\'.,:+-=%#@_";

    public final static String sanitize(String input) {

        String output = "";

        for (int i=0; i<input.length(); i++){
              if (allowed.indexOf(input.charAt(i))>=0) {
                  output += input.charAt(i);
              }
            }
        return escape(output);
    }


    public static String escape(String input) {

        String output = StringEscapeUtils.escapeJava(input);

        return output;
    }

    public static String unescape(String input) {

        String output = StringEscapeUtils.unescapeJava(input);

        return output;
    }

    public static String escapeForSql(String input) {


    }

    public static String unescapeFromSql(String input) {

    }

}