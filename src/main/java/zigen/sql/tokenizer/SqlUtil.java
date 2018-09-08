/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.tokenizer;

public class SqlUtil {
	// 改行コード
	public static final String NEW_LINE = System.getProperty("line.separator");

	private static final String NEW_LINE_CODE;

	private static final String[][] CODE = {
			{ "StringBuffer sb = new StringBuffer();", "sb.append(\"",
					".append(\"", "sb.toString();" },
			{ "StringBuilder sb = new StringBuilder();", "sb.append(\"",
					".append(\"", "sb.toString();" },
			{ "StringBuilder sb = new StringBuilder();", "sb.Append(\"",
					".Append(\"", "sb.ToString();" } };

	private static final int CODE_JAVA_STRING_BUFFER = 0;

	private static final int CODE_JAVA_STRING_BUILDER = 1;

	private static final int CODE_C_SHARP_STRING_BUILDER = 2;

	static {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < NEW_LINE.length(); i++) {
			char c = NEW_LINE.charAt(i);

			switch (c) {
			case '\r':
				sb.append("\\r");
				break;
			case '\n':
				sb.append("\\n");
			}
		}

		NEW_LINE_CODE = sb.toString();
	}

	/**
	 * 文字列をStringBufferコードに変換します。
	 * 
	 * @return
	 */
	public static String convertStringBufferForJava(String str) {
		return convertSqlToCode(str, CODE_JAVA_STRING_BUFFER, false);
	}

	public static String convertStringBufferForJava(String str, boolean newLine) {
		return convertSqlToCode(str, CODE_JAVA_STRING_BUFFER, newLine);
	}

	/**
	 * 文字列をStringBuilderコードに変換します。(Java用)
	 * 
	 * @return
	 */
	public static String convertStringBuilderForJava(String str) {
		return convertSqlToCode(str, CODE_JAVA_STRING_BUILDER, false);
	}

	public static String convertStringBuilderForJava(String str, boolean newLine) {
		return convertSqlToCode(str, CODE_JAVA_STRING_BUILDER, newLine);
	}

	/**
	 * 文字列をStringBuilderコードに変換します。(C#用)
	 * 
	 * @return
	 */
	public static String convertStringBuilderForCSharp(String str) {
		return convertSqlToCode(str, CODE_C_SHARP_STRING_BUILDER, false);
	}

	public static String convertStringBuilderForCSharp(String str,
			boolean newLine) {
		return convertSqlToCode(str, CODE_C_SHARP_STRING_BUILDER, newLine);
	}

	/**
	 * 文字列を指定されたコードに変換します。
	 * 
	 * @param str
	 * @param type
	 * @return
	 */
	private static String convertSqlToCode(String str, int type, boolean newLine) {
		if (str == null)
			return null;

		StringBuffer sb = new StringBuffer(str.length() * 2);
		sb.append(CODE[type][0]);

		String[] strs = str.split(NEW_LINE);

		for (int i = 0; i < strs.length; i++) {
			sb.append(NEW_LINE);
			sb.append(CODE[type][1]);

			String lineStr = strs[i];
			int lineLen = lineStr.length();
			for (int j = 0; j < lineLen; j++) {
				char c = lineStr.charAt(j);
				if (c == '"') {
					sb.append('\\');
				}
				sb.append(c);
			}
			sb.append("\")");

			if (newLine) {
				sb.append(CODE[type][2]);
				sb.append(NEW_LINE_CODE);
				sb.append("\")");
			}

			sb.append(';');
		}

		sb.append(NEW_LINE);
		sb.append(CODE[type][3]);
		return sb.toString();
	}

	/**
	 * ソースコードからSQL文を抜き出します。<br>
	 * （例）『sb.Append(\"SELECT\");\r\n");』 ⇒ 『SELECT』
	 * 
	 * @param str
	 * @return
	 */
	public static String convertCodeToSql(String str) {
		if (str == null)
			return null;

		StringBuffer sb = new StringBuffer();
		int len = str.length();
		// boolean isIntoQuat = false;
		int pos = 0;

		while (pos < len) {
			int startPos = str.indexOf("(\"", pos);
			if (startPos == -1)
				break;

			int newLinePos = str.indexOf(");", pos);

			if (newLinePos > -1 && newLinePos < startPos) {
				sb.append(NEW_LINE);
				pos = newLinePos + 1;
				continue;
			}

			startPos += 2;
			int endPos = str.indexOf("\")", startPos);
			if (endPos == -1) {
				sb.append(str.substring(startPos, len));
				break;
			} else {
				sb.append(str.substring(startPos, endPos));
				pos = endPos + 1;
			}
		}

		return sb.toString();
	}
}
