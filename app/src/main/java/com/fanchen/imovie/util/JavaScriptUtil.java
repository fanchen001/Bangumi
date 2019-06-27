package com.fanchen.imovie.util;

import android.text.TextUtils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fanchen on 2017/10/28.
 */
public class JavaScriptUtil {

    public static boolean isJson(String match){
        return (match.indexOf("{") == 0 && match.lastIndexOf("}") == match.length() - 1) || match.indexOf("[") == 0 && match.lastIndexOf("]") == match.length() - 1;
    }

    /*
    * 对应javascript的escape()函数, 加码后的串可直接使用javascript的unescape()进行解码
    */
    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    /*
    * 对应javascript的unescape()函数, 可对javascript的escape()进行解码
    */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static String match(String regex, String input, int group) {
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                return matcher.group(group);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String match(String regex, String input, int group, int start, int endOf) {
        try {
            String match = match(regex, input, group);
            if (!TextUtils.isEmpty(match) && start >= 0 && match.length() - endOf >= 0)
                return match.substring(start, match.length() - endOf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param jsCode
     * @return
     */
    public static String evalDecrypt(String jsCode) {
        try {
            Context rhino = Context.enter();
            rhino.setOptimizationLevel(-1);
            Scriptable scope = rhino.initStandardObjects();
            Object object = rhino.evaluateString(scope, jsCode, null, 1, null);
            return Context.toString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param jsCode
     * @return
     */
    public static String callFunction(String jsCode) {
        try {
            Context rhino = Context.enter();
            rhino.setOptimizationLevel(-1);
            Scriptable scope = rhino.initStandardObjects();
            Object object = rhino.evaluateString(scope, jsCode, null, 1, null);
            if (object instanceof org.mozilla.javascript.Function) {
                org.mozilla.javascript.Function f = (org.mozilla.javascript.Function) object;
                Object call = f.call(rhino, f, null, new Object[]{1});
                return call.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * @param eval
     * @return
     */

    public static String getKkkkmaoE2(String eval) {
        String evalDecrypt = JavaScriptUtil.evalDecrypt(eval);
        evalDecrypt = "function(){" + evalDecrypt.replace("eval(\"do\"+\"cum\"+\"en\"+\"t.ge\"+\"tEle\"+\"men\"+\"tB\"+\"yId('e'+'2').va\"+\"lue=e1r.join('')\");", "return e1r.join('');}");
        return JavaScriptUtil.callFunction(evalDecrypt);
    }
}
