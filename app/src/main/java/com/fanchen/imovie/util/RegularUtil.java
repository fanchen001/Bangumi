/*
 * Copyright (c) 2013-2014, thinkjoy Inc. All Rights Reserved.
 * 
 * Project Name: Jiaxiao
 * $Id: RegularUtil.java 2014-8-20 上午10:49:13 $ 
 */
package com.fanchen.imovie.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * 正则表达式
 * <p/>
 * 
 * @author fanchen
 * @version
 */
public class RegularUtil {
	private static final String TAG = "RegularUtil";
	static boolean flag = false;
    static String regex = "";

	/********************************* 常用正则表达式代码 ********************/
	/**
	 * 常用正则表达式代码提供 验证数字：^[0-9]*$ <br>
	 * 验证n位的数字：^\d{n}$ <br>
	 * 验证至少n位数字：^\d{n,}$ <br>
	 * 验证m-n位的数字：^\d{m,n}$ <br>
	 * 验证零和非零开头的数字：^(0|[1-9][0-9]*)$ <br>
	 * 验证有两位小数的正实数：^[0-9]+(.[0-9]{2})?$ <br>
	 * 验证有1-3位小数的正实数：^[0-9]+(.[0-9]{1,3})?$ <br>
	 * 验证非零的正整数：^\+?[1-9][0-9]*$ <br>
	 * 验证非零的负整数：^\-[1-9][0-9]*$ <br>
	 * 验证非负整数（正整数 + 0） ^\d+$ <br>
	 * 验证非正整数（负整数 + 0） ^((-\d+)|(0+))$ <br>
	 * 验证长度为3的字符：^.{3}$ <br>
	 * 验证由26个英文字母组成的字符串：^[A-Za-z]+$ <br>
	 * 验证由26个大写英文字母组成的字符串：^[A-Z]+$ <br>
	 * 验证由26个小写英文字母组成的字符串：^[a-z]+$ <br>
	 * 验证由数字和26个英文字母组成的字符串：^[A-Za-z0-9]+$ <br>
	 * 验证由数字、26个英文字母或者下划线组成的字符串：^\w+$ <br>
	 * 验证用户密码:^[a-zA-Z]\w{5,17}$ 正确格式为：以字母开头，长度在6-18之间，只能包含字符、数字和下划线。 <br>
	 * 验证是否含有 ^%&',;=?$\" 等字符：[^%&',;=?$\x22]+ <br>
	 * 验证汉字：^[\u4E00-\u9FA5\uF900-\uFA2D]+$ <br>
	 * 验证Email地址：^\w+[-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$ <br>
	 * 验证InternetURL：^http://([\w-]+\.)+[\w-]+(/[\w-./?%&=]*)?$
	 * ；^[a-zA-z]+://(w+(-w+)*)(.(w+(-w+)*))*(?S*)?$ <br>
	 * 验证手机号码：^(\(\d{3,4}\)|\d{3,4}-)?\d{7,8}$：--正确格式为：XXXX-XXXXXXX，XXXX-
	 * XXXXXXXX，XXX-XXXXXXX，XXX-XXXXXXXX，XXXXXXX，XXXXXXXX。 <br>
	 * 验证身份证号（15位或18位数字）：^\d{15}|\d{}18$ <br>
	 * 验证一年的12个月：^(0?[1-9]|1[0-2])$ 正确格式为：“01”-“09”和“1”“12” <br>
	 * 验证一个月的31天：^((0?[1-9])|((1|2)[0-9])|30|31)$ 正确格式为：01、09和1、31。 <br>
	 * 整数：^-?\d+$ <br>
	 * 非负浮点数（正浮点数 + 0）：^\d+(\.\d+)?$ <br>
	 * 正浮点数
	 * ^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9
	 * ][0-9]*))$ <br>
	 * 非正浮点数（负浮点数 + 0） ^((-\d+(\.\d+)?)|(0+(\.0+)?))$ <br>
	 * 负浮点数
	 * ^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1
	 * -9][0-9]*)))$ <br>
	 * 浮点数 ^(-?\d+)(\.\d+)?$ <br>
	 */
	/**
	 * 正则表达式判断字符串是否全是数字
	 * 
	 * @param strInput
	 * @return
	 */
	public static boolean isAllNumric(String strInput) {
		if (strInput != null && !strInput.trim().equalsIgnoreCase("")) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher matcher = pattern.matcher(strInput);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 正则表达式判断字符串是否全是字母
	 * 
	 * @param strInput
	 * @return
	 */
	public static boolean isAllLetter(String strInput) {
		if (strInput != null && !strInput.trim().equalsIgnoreCase("")) {
			Pattern pattern = Pattern.compile("[A-Za-z]+");
			Matcher matcher = pattern.matcher(strInput);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 正则表达式判断字符串是否全是中文汉字
	 * 
	 * @param strInput
	 * @return
	 */
	public static boolean isAllChinese(String strInput) {
		if (strInput != null && !strInput.trim().equalsIgnoreCase("")) {
			// 此正则表达式用来判断是否为中文(\\u4E00-\\u9FA5\\uF900-\\uFA2D是指汉字的Unicode编码范围 )
			Pattern pattern = Pattern.compile("[\u4E00-\u9FA5\uF900-\uFA2D]+");
			Matcher matcher = pattern.matcher(strInput);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 正则表达式判断字符串是否含有中文汉字
	 * 
	 * @param strInput
	 * @return
	 */
	public static boolean isHaveChinese(String strInput) {
		if (strInput != null && !strInput.trim().equalsIgnoreCase("")) {
			for (int i = 0; i < strInput.length(); i++) {
				char ss = strInput.charAt(i);
				boolean s = String.valueOf(ss).matches(
						"[\u4E00-\u9FA5\uF900-\uFA2D]");
				if (s) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	/**
	 * 正则表达式判断字符串是否满足中文、大小写字母、数字、下划线和长度限制
	 * 
	 * @param strInput
	 *            字符串
	 * @param intMax
	 *            字符串的设定长度
	 * @return
	 */
	public static boolean isStringOK(String strInput, int intMax) {
		/** 此正则表达式将上面二者结合起来进行判断，中文、大小写字母和数字，{2，10}字符的长度为2-10 **/
		if (strInput != null && !strInput.trim().equalsIgnoreCase("")) {
			String Regular = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{2," + intMax
					+ "}$";
			Pattern pattern = Pattern
					.compile(Regular, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(strInput);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 验证字符串是否是邮箱地址
	 * 
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strInput) {
		if (strInput != null && !strInput.trim().equalsIgnoreCase("")) {
			String Regular = "^([a-z0-9A-Z]+[-|\\_.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern pattern = Pattern
					.compile(Regular, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(strInput);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 验证是否是QQ号码
	 * 
	 * @param strQQ
	 * @return
	 */
	public static boolean isQQ(String strInput) {
		if (strInput != null && !strInput.trim().equalsIgnoreCase("")) {
			Pattern pattern = Pattern.compile("[1-9]{5,10}");
			Matcher matcher = pattern.matcher(strInput);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 返回正则表达式匹配的结果
	 * 
	 * @param strInput
	 * @return
	 */
	public static ArrayList<String> getRegexMatcherResults(String strSource,
			String strRegex) {
		try {
			if (strSource != null && !strSource.trim().equalsIgnoreCase("")) {
				Pattern pattern = Pattern.compile(strRegex);
				Matcher matcher = pattern.matcher(strSource);
				ArrayList<String> RegexMatcherResults = new ArrayList<String>();
				while (matcher.find()) {
					Log.e(TAG, "getRegexMatcherResults=" + matcher.group(1));
					RegexMatcherResults.add(matcher.group(1));
				}
				return RegexMatcherResults;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(TAG,
					"getRegexMatcherResults is Error! errorCode = "
							+ ex.getMessage());
		}
		return null;
	}

	/**
	 * 通过正则表达式提取字串
	 * 
	 * @param str
	 *            传入的字符串
	 * @param regEx
	 *            正则表达式
	 * @return 要获取的字符串
	 */
	public static String getRegexString(String strMsg) {
		String strReturn = "";
		int a = strMsg.indexOf("{");
		int b = strMsg.lastIndexOf("}");
		strReturn = strMsg.substring(a, b + 1);
		return strReturn;
	}

	/**
	 * 判断是否为合法IP
	 * 
	 * @return the ip
	 */
	public boolean isboolIp(String ipAddress) {
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	/**
	 * 验证手机号码
	 * 
	 * 移动号码段:139、138、137、136、135、134、150、151、152、157、158、159、182、183、187、188、147
	 * 联通号码段:130、131、132、136、185、186、145 电信号码段:133、153、180、189
	 * 
	 * @param cellphone
	 * @return
	 */
	public static boolean checkCellphone(String cellphone) {
		String regex = "^((14[0-9])|(13[0-9])|(17[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
		return check(cellphone, regex);
	}

	public static boolean check(String str, String regex) {
		try {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
}
