package com.fanchen.imovie.view.webview;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 安全WebView
 *
 * @author Toby
 */
public class SafeWebView extends WebView {
    private static final boolean DEBUG = false;
    private static final String VAR_ARG_PREFIX = "arg";
    private static final String MSG_PROMPT_HEADER = "MyApp:";
    private static final String KEY_INTERFACE_NAME = "obj";
    private static final String KEY_FUNCTION_NAME = "func";
    private static final String KEY_ARG_ARRAY = "args";
    private static final String[] mFilterMethods = {"getClass", "hashCode", "notify", "notifyAll", "equals",
            "toString", "wait",};

    private final HashMap<String, Object> mJsInterfaceMap = new HashMap<String, Object>();
    private String mJsStringCache = null;

    private boolean isCanPullDown = true;//默认可以下拉

    private OnTouchScrollListener onTouchScrollListener;

    public SafeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SafeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SafeWebView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // 删除掉Android默认注册的JS接口
        removeSearchBoxImpl();
        WebSettings webSetting = getSettings();
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setDatabaseEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setDomStorageEnabled(true);// 开启DOM
        webSetting.setAllowFileAccess(true);// 设置支持文件流
        webSetting.setUseWideViewPort(true);// 调整到适合webview大小
        webSetting.setLoadWithOverviewMode(true);// 调整到适合webview大小
        webSetting.setAppCacheEnabled(true);// 开启缓存机制
        webSetting.setAppCacheMaxSize(64 * 1024 * 1024);
        webSetting.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        webSetting.setAppCachePath(context.getDir("cache", Context.MODE_PRIVATE).getPath());
        webSetting.setGeolocationDatabasePath(context.getDir("database", Context.MODE_PRIVATE).getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(0);
        }
    }

    private void removeSearchBoxImpl() {
        try {
            //if (hasHoneycomb() && !hasJellyBeanMR1()) {
            invokeMethod("removeJavascriptInterface", "searchBoxJavaBridge_");
            //}
        } catch (Exception e) {
        }

        try {
            //if (hasHoneycomb() && !hasJellyBeanMR1()) {
            invokeMethod("removeJavascriptInterface", "accessibility");
            //}
        } catch (Exception e) {
        }

        try {
            //if (hasHoneycomb() && !hasJellyBeanMR1()) {
            invokeMethod("removeJavascriptInterface", "accessibilityTraversal");
            //}
        } catch (Exception e) {
        }
    }

    private void invokeMethod(String method, String param) {
        Method m;
        try {
            m = WebView.class.getDeclaredMethod(method, String.class);
            m.setAccessible(true);
            m.invoke(this, param);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onTouchScrollListener != null)
                    onTouchScrollListener.touch(this.getScrollY());
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setOnTouchScrollListener(OnTouchScrollListener onTouchScrollListener) {
        this.onTouchScrollListener = onTouchScrollListener;
    }

    /**
     * 若要删除JavaScript，请调用该方法
     * 注意：切记不要调用RemoveJavaScriptInterface，否则会有异常
     * <p>
     * 原因：
     * Android 2.x的RemoveJavaScriptInterface不能直接访问，必须通过反射，更不能用Super来调用。
     * 而恰好原来的代码就覆写了它，也就是说，那个系统的方法已经被彻底“覆盖”，永远没有调到的机会，自然也就不能删掉SearchBox了
     */
    public void removeJsInterface(String interfaceName) {
        if (hasJellyBeanMR1()) {
            invokeMethod("removeJavascriptInterface", interfaceName);
        } else {
            mJsInterfaceMap.remove(interfaceName);
            mJsStringCache = null;
            injectJavascriptInterfaces();
        }
    }

    @Override
    public void addJavascriptInterface(Object obj, String interfaceName) {

        if (TextUtils.isEmpty(interfaceName)) {
            return;
        }
        mJsInterfaceMap.put(interfaceName, obj);
        injectJavascriptInterfaces();
    }

    public void addJavascriptInterfaces(Object obj, String interfaceName) {
        super.addJavascriptInterface(obj, interfaceName);
    }

    public boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= 11;
    }

    public boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= 17;
    }

    public boolean handleJsInterface(WebView view, String url, String message, String defaultValue,
                                     JsPromptResult result) {
        String prefix = MSG_PROMPT_HEADER;
        if (!message.startsWith(prefix)) {
            return false;
        }

        String jsonStr = message.substring(prefix.length());
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            String interfaceName = jsonObj.getString(KEY_INTERFACE_NAME);
            String methodName = jsonObj.getString(KEY_FUNCTION_NAME);
            JSONArray argsArray = jsonObj.getJSONArray(KEY_ARG_ARRAY);
            Object[] args = null;
            if (null != argsArray) {
                int count = argsArray.length();
                if (count > 0) {
                    args = new Object[count];

                    for (int i = 0; i < count; ++i) {
                        args[i] = argsArray.get(i);
                    }
                }
            }

            if (invokeJSInterfaceMethod(result, interfaceName, methodName, args)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.cancel();
        return false;
    }

    private boolean invokeJSInterfaceMethod(JsPromptResult result, String interfaceName, String methodName,
                                            Object[] args) {

        boolean succeed = false;
        final Object obj = mJsInterfaceMap.get(interfaceName);
        if (null == obj) {
            result.cancel();
            return false;
        }

        Class<?>[] parameterTypes = null;
        int count = 0;
        if (args != null) {
            count = args.length;
        }

        if (count > 0) {
            parameterTypes = new Class[count];
            for (int i = 0; i < count; ++i) {
                parameterTypes[i] = getClassFromJsonObject(args[i]);
            }
        }

        try {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            Object returnObj = method.invoke(obj, args); // 执行接口调用
            boolean isVoid = returnObj == null || returnObj.getClass() == void.class;
            String returnValue = isVoid ? "" : returnObj.toString();
            result.confirm(returnValue); // 通过prompt返回调用结果
            succeed = true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.cancel();
        return succeed;
    }

    private Class<?> getClassFromJsonObject(Object obj) {
        Class<?> cls = obj.getClass();

        // js对象只支持int boolean string三种类型
        if (cls == Integer.class) {
            cls = Integer.TYPE;
        } else if (cls == Boolean.class) {
            cls = Boolean.TYPE;
        } else {
            cls = String.class;
        }

        return cls;
    }

    public void injectJavascriptInterfaces(WebView webView) {
        // if (webView instanceof CommonWebView) {
        injectJavascriptInterfaces();
        // }
    }

    public void injectJavascriptInterfaces() {
        if (!TextUtils.isEmpty(mJsStringCache)) {
            loadJavascriptInterfaces();
            return;
        }

        String jsString = genJavascriptInterfacesString();
        mJsStringCache = jsString;

        loadJavascriptInterfaces();
    }

    private void loadJavascriptInterfaces() {
        try {
            if (!TextUtils.isEmpty(mJsStringCache)) {
                this.loadUrl(mJsStringCache);
            }
        } catch (Exception e) {

        }
    }

    private String genJavascriptInterfacesString() {
        if (mJsInterfaceMap.size() == 0) {
            mJsStringCache = null;
            return null;
        }

        /*
         * 要注入的JS的格式，其中XXX为注入的对象的方法名，例如注入的对象中有一个方法A，那么这个XXX就是A
         * 如果这个对象中有多个方法，则会注册多个window.XXX_js_interface_name块，我们是用反射的方法遍历
         * 注入对象中的所有带有@JavaScripterInterface标注的方法
         *
         * javascript:(function JsAddJavascriptInterface_(){
         * if(typeof(window.XXX_js_interface_name)!='undefined'){
         * console.log('window.XXX_js_interface_name is exist!!'); }else{
         * window.XXX_js_interface_name={ XXX:function(arg0,arg1){ return
         * prompt(
         * 'MyApp:'+JSON.stringify({obj:'XXX_js_interface_name',func:'XXX_',args:[arg0,arg1]}));
         * }, }; } })()
         */

        Iterator<Entry<String, Object>> iterator = mJsInterfaceMap.entrySet().iterator();
        // Head
        StringBuilder script = new StringBuilder();
        script.append("javascript:(function JsAddJavascriptInterface_(){");

        // Add methods
        try {
            while (iterator.hasNext()) {
                Entry<String, Object> entry = iterator.next();
                String interfaceName = entry.getKey();
                Object obj = entry.getValue();

                createJsMethod(interfaceName, obj, script);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // End
        script.append("})()");
        return script.toString();
    }

    private void createJsMethod(String interfaceName, Object obj, StringBuilder script) {
        if (TextUtils.isEmpty(interfaceName) || (null == obj) || (null == script)) {
            return;
        }

        Class<? extends Object> objClass = obj.getClass();

        script.append("if(typeof(window.").append(interfaceName).append(")!='undefined'){");
        if (DEBUG) {
            script.append("    console.log('window." + interfaceName + "_js_interface_name is exist!!');");
        }

        script.append("}else {");
        script.append("    window.").append(interfaceName).append("={");

        // Add methods
        Method[] methods = objClass.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            // 过滤掉Object类的方法，包括getClass()方法，因为在Js中就是通过getClass()方法来得到Runtime实例
            if (filterMethods(methodName)) {
                continue;
            }

            script.append("        ").append(methodName).append(":function(");
            // 添加方法的参数
            int argCount = method.getParameterTypes().length;
            if (argCount > 0) {
                int maxCount = argCount - 1;
                for (int i = 0; i < maxCount; ++i) {
                    script.append(VAR_ARG_PREFIX).append(i).append(",");
                }
                script.append(VAR_ARG_PREFIX).append(argCount - 1);
            }

            script.append(") {");

            // Add implementation
            if (method.getReturnType() != void.class) {
                script.append("            return ").append("prompt('").append(MSG_PROMPT_HEADER).append("'+");
            } else {
                script.append("            prompt('").append(MSG_PROMPT_HEADER).append("'+");
            }

            // Begin JSON
            script.append("JSON.stringify({");
            script.append(KEY_INTERFACE_NAME).append(":'").append(interfaceName).append("',");
            script.append(KEY_FUNCTION_NAME).append(":'").append(methodName).append("',");
            script.append(KEY_ARG_ARRAY).append(":[");
            // 添加参数到JSON串中
            if (argCount > 0) {
                int max = argCount - 1;
                for (int i = 0; i < max; i++) {
                    script.append(VAR_ARG_PREFIX).append(i).append(",");
                }
                script.append(VAR_ARG_PREFIX).append(max);
            }

            // End JSON
            script.append("]})");
            // End prompt
            script.append(");");
            // End function
            script.append("        }, ");
        }

        // End of obj
        script.append("    };");
        // End of if or else
        script.append("}");
    }

    private boolean filterMethods(String methodName) {
        for (String method : mFilterMethods) {
            if (method.equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置是否可以下拉
     *
     * @param canPullDown
     */
    public void setCanPullDown(boolean canPullDown) {
        isCanPullDown = canPullDown;
    }


    public interface OnTouchScrollListener{

        void touch(int scrollY);

    }
}
