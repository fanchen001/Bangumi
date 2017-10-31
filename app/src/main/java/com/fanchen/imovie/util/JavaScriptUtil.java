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

    public static String match(String regex, String input, int group,int start,int endOf) {
        try {
            String match = match(regex, input, group);
            if(!TextUtils.isEmpty(match) && start >=0 && match.length() - endOf >= 0)
            return match.substring(start,match.length() - endOf);
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
     *
     * @param eval
     * @return
     */
    public static String getKkkkmaoE2(String eval){
        String evalDecrypt = JavaScriptUtil.evalDecrypt(eval);
        evalDecrypt = "function(){" + evalDecrypt.replace("eval(\"do\"+\"cum\"+\"en\"+\"t.ge\"+\"tEle\"+\"men\"+\"tB\"+\"yId('e'+'2').va\"+\"lue=e1r.join('')\");","return e1r.join('');}");
        return JavaScriptUtil.callFunction(evalDecrypt);
    }
}
