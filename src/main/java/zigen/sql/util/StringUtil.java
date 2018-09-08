/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.util;

import java.util.Arrays;

public class StringUtil {

	/**
	 * 指定した文字で左パディングします。
	 * 
	 * @param s
	 * @param scale
	 * @param padChar
	 * @return
	 */
	public static String padLeft(String s, int scale, char padChar) {
		if (s == null || s.length() >= scale)
			return s;

		StringBuffer sb = new StringBuffer(scale);
		int loop = scale - s.length();

		for (int i = 0; i < loop; i++)
			sb.append(padChar);
		sb.append(s);

		return sb.toString();
	}

	/**
	 * 指定した文字で右パディングします。
	 * 
	 * @param s
	 * @param scale
	 * @param padChar
	 * @return
	 */
	public static String padRight(String s, int scale, char padChar) {
		if (s == null || s.length() >= scale)
			return s;

		StringBuffer sb = new StringBuffer(scale);
		int loop = scale - s.length();

		sb.append(s);
		for (int i = 0; i < loop; i++)
			sb.append(padChar);

		return sb.toString();
	}

	/**
	 * 指定した文字で中央パディングします。
	 * 
	 * @param s
	 * @param scale
	 * @param padChar
	 * @return
	 */
	public static String padCenter(String s, int scale, char padChar) {
		if (s == null || s.length() >= scale)
			return s;

		StringBuffer sb = new StringBuffer(scale);
		int loop = (scale - s.length()) / 2;

		for (int i = 0; i < loop; i++)
			sb.append(padChar);
		String padString = sb.toString();
		sb.append(s);
		sb.append(padString);
		if (sb.length() != scale)
			sb.append(padChar);

		return sb.toString();
	}

	/**
	 * 指定された文字で文字列を結合します。
	 * 
	 * @param strs
	 * @param c
	 * @return
	 */
	public static String union(String[] strs, char c) {
		if (strs == null)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			sb.append(strs[i]);
			sb.append(c);
		}
		sb.deleteCharAt(sb.length() - 1); // 最後のcを削除

		return sb.toString();
	}

	/**
	 * 指定した文字を左トリムした文字列を返します。
	 * 
	 * @param str
	 * @param charArray
	 * @return
	 */
	public static String leftTrim(String str, char[] charArray) {
		if (str == null || charArray == null)
			return str;

		Arrays.sort(charArray);

		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (0 > Arrays.binarySearch(charArray, c))
				return str.substring(i);
		}
		return str;
	}

	/**
	 * 指定した文字を右トリムした文字列を返します。
	 * 
	 * @param str
	 * @param charArray
	 * @return
	 */
	public static String rightTrim(String str, char[] charArray) {
		if (str == null || charArray == null)
			return str;

		Arrays.sort(charArray);

		int len = str.length();
		for (int i = len - 1; i > 0; i--) {
			char c = str.charAt(i);
			if (0 > Arrays.binarySearch(charArray, c))
				return str.substring(0, i);
		}
		return str;
	}

	/**
	 * 指定した文字を前後トリムした文字列を返します。
	 * 
	 * @param str
	 * @param charArray
	 * @return
	 */
	public static String Trim(String str, char[] charArray) {
		if (str == null || charArray == null)
			return str;

		Arrays.sort(charArray);

		return rightTrim(leftTrim(str, charArray), charArray);
	}

	/**
	 * 指定した文字を頭大文字で返します。
	 * 
	 * @param str
	 * @return
	 */
	public static String toCapitalcase(String str) {
		if (str == null || str.length() == 0)
			return str;

		StringBuffer sb = new StringBuffer(str.toLowerCase());
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

	/**
	 * 文字配列を全て大文字に変換します。
	 * 
	 * @param strs
	 * @return
	 */
	public static String[] toUpperCase(String[] strs) {
		if (strs == null || strs.length == 0)
			return strs;

		for (int i = 0; i < strs.length; i++) {
			strs[i] = strs[i].toUpperCase();
		}
		return strs;
	}
}
