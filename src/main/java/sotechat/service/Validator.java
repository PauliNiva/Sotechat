package sotechat.service;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by varkoi on 16.6.2016.
 */
public class Validator {

    private static String allowed = "0123456789abcdefghijklmnopqrstuvxyzåäö()*!?\"\'.,:;+-=%&#@_";

    public static String sanitize(String input) {

        String output = "";

        for (int i=0; i<input.length(); i++){
              if (allowed.indexOf(input.charAt(i))>=0){
                  output += input.charAt(i);
              }
            }
        return escape(output);
    }


    public static String escape(String input){

        String output = StringEscapeUtils.escapeJava(input);
        output = StringEscapeUtils.escapeXml(output);

        return output;
    }

    public static String unescape(String input){

        String output = StringEscapeUtils.unescapeXml(input);
        output = StringEscapeUtils.unescapeJava(output);

        return output;
    }


}