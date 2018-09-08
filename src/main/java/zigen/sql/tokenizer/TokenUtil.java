/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.tokenizer;

import java.util.Arrays;

public class TokenUtil {
	// 改行コード
	public static final String NEW_LINE_SYSTEM = System
			.getProperty("line.separator");

	// 改行文字列 ※順序変更禁止
	public static final String[] NEW_LINES = { "\r\n", "\r", "\n" };
	// 改行文字列（デバッグ用）
	public static final String[] DEBUG_NEW_LINES = { "★CRLF★", "★CR★", "★LF★" };
	// 改行正規表現
	public static final String NEW_LINES_REGEX;
	// 改行文字
	public static final char[] NEW_LINE_CHAR;

	// タイプ
	public static final int TYPE_BEGIN_SQL = 0;// SQL開始
	public static final int TYPE_KEYWORD = 10; // キーワード
	public static final int TYPE_SYMBOL = 20; // 記号
	public static final int TYPE_OPERATOR = 30; // 演算子
	public static final int TYPE_NAME = 40; // 名称
	public static final int TYPE_VALUE = 50; // 値
	public static final int TYPE_COMMENT = 60; // コメント
	public static final int TYPE_NEW_LINE = 70; // 改行
	public static final int TYPE_SQL_SEPARATE = 80; // SQL区切り
	public static final int TYPE_EMPTY_LINE = 90; // 空行
	public static final int TYPE_END_SQL = 100; // SQL終了
	public static final int TYPE_UNKNOWN = -1; // 不明

	// サブタイプ
	public static final int SUBTYPE_DEFAULT = 0; // デフォルト
	public static final int SUBTYPE_KEYWORD_DATATYPE = 11; // データ型キーワード
	public static final int SUBTYPE_KEYWORD_FUNCTION = 12; // 関数キーワード
	public static final int SUBTYPE_VALUE_STRING = 51; // 文字値
	public static final int SUBTYPE_VALUE_NUMERIC = 52; // 数値
	public static final int SUBTYPE_VALUE_BIND = 53; // バインド変数
	public static final int SUBTYPE_COMMENT_SINGLE = 61; // 一行コメント
	public static final int SUBTYPE_COMMENT_MULTI = 62; // 複数行コメント

	// ワード
	public static final String[] KEYWORD = { "ACCESS", "ADD", "ALL", "ALTER",
			"AND", "ANY", "AS", "ASC", "AUDIT", "BETWEEN", "BEGIN", "BOTH",
			"BY", "CACHE", "CASCADE", "CASE", "CHAR", "CHECK", "CLUSTER",
			"COLUMN", "COMMENT", "COMMIT", "COMPRESS", "CONNECT", "CONSTRAINT",
			"CREATE", "CROSS", "CURRENT", "CYCLE", "DATE", "DECIMAL",
			"DECLARE", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE",
			"END", "ESCAPE", "EXCEPT", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT",
			"FUNCTION", "FOR", "FOREIGN", "FROM", "GRANT", "GROUP", "HAVING",
			"IDENTIFIED", "IF", "IMMEDIATE", "IN", "INCREMENT", "INDEX",
			"INITIAL", "INNER", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS",
			"JOIN", "KEY", "LEADING", "LEVEL", "LEFT", "LIKE", "LOCK", "LONG",
			"MERGE", "MATCH", "MATCHED", "MAXEXTENTS", "MAXVALUE", "MINUS",
			"MINVALUE", "MLSLABEL", "MODE", "MODIFY", "NATURAL", "NOAUDIT",
			"NOCOMPRESS", "NOCYCLE", "NOMAXVALUE", "NOMINVALUE", "NOT",
			"NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE",
			"ONLY", "OPTION", "OR", "ORDER", "OUTER", "OVER", "PACKAGE",
			"PARTITION", "PCTFREE", "PRIMARY", "PRIOR", "PRIVILEGES",
			"PROCEDURE", "PUBLIC", "RAW", "READ", "RENAME", "RESOURCE",
			"RETURN", "REVOKE", "RIGHT", "ROLLBACK", "ROW", "ROWID", "ROWNUM",
			"ROWS", "SCHEMA", "SELECT", "SEQUENCE", "SESSION", "SET", "SHARE",
			"SIZE", "SMALLINT", "SHOW", "START", "SUCCESSFUL", "SYNONYM",
			"SYSDATE", "TABLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TO",
			"TRAILING", "TRIGGER", "TRUNCATE", "TYPE", "UID", "UNION",
			"UNIQUE", "UPDATE", "USER", "USING", "VALIDATE", "VALUES",
			"VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHEN", "WHERE", "WITH" };

	// データ型
	public static final String[] KEYWORD_DATATYPE = { "BFILE", "BINARY_DOUBLE",
			"BINARY_FLOAT", "BLOB", "CHAR", "CHARACTER", "CHAR VARYING",
			"CHARACTER VARYING", "CLOB", "DATE", "DEC", "DECIMAL",
			"DOUBLE PRECISION", "INTERVAL YEAR TO MONTH", "INT", "INTEGER",
			"INTERVAL", "INTERVAL DAY TO SECOND", "LONG", "LONG RAW",
			"NATIONAL CHAR", "NATIONAL CHARACTER",
			"NATIONAL CHARACTER VARYING", "NATIONAL CHAR VARYING", "NCHAR",
			"NCHAR VARYING", "NUMBER", "NUMERIC", "NVARCHAR2", "RAW", "REAL",
			"ROWID", "SMALLINT", "TIME", "TIMESTAMP",
			"TIMESTAMP WITH LOCAL TIMEZONE", "TIMESTAMP WITH TIMEZONE",
			"VARCHAR", "VARCHAR2" };

	// 関数
	public static final String[] KEYWORD_FUNCTION = { "ABS", "ACOS",
			"ADD_MONTHS", "ASCII", "ASIN", "ATAN", "AVG", "CEIL",
			"CHARTOROWID", "CHECK", "CHR", "COALESCE", "CONCAT", "CONVERT",
			"COS", "COSH", "COUNT", "DECODE", "DUMP", "EXP", "FLOOR",
			"GREATEST", "HEXTORAW", "INITCAP", "INSTR", "INSTRB", "LAST_DAY",
			"LEAST", "LENGTH", "LENGTHB", "LN", "LOG", "LOWER", "LPAD",
			"LTRIM", "MAX", "MIN", "MOD", "MONTHS_BETWEEN", "NEXT_DAY",
			"NULLIF", "NVL", "NVL2", "POWER", "RAWTOHEX", "REPLACE", "ROUND",
			"ROWIDTOCHAR", "ROW_NUMBER", "RPAD", "RTRIM", "SIGN", "SIN",
			"SINH", "SQRT", "STDDEV", "SUBSTR", "SUBSTRB", "SUM", "SYSDATE",
			"TAN", "TANH", "TO_CHAR", "TO_DATE", "TO_MULTI_BYTE", "TO_NUMBER",
			"TO_SINGLE_BYTE", "TRIM", "TRUNC", "UID", "UPPER", "USER",
			"USERENV", "VARIANCE", "VSIZE" };

	// SQL開始キーワード
	public static final String[] BEGIN_SQL_KEYWORD = { "ALTER", "COMMENT",
			"CREATE", "DELETE", "DROP", "GRANT", "INSERT", "MARGE", "REVOKE",
			"SELECT", "TRUNCATE", "UPDATE" };

	// 複合キーワード
	public static final String[] MULTI_KEYWORD = { "CREATE OR REPLACE",
			"CREATE", "CROSS JOIN", "COMMENT ON", "FOR UPDATE", "FULL JOIN",
			"FULL OUTER JOIN", "GROUP BY", "INCREMENT BY", "INNER JOIN",
			"JOIN", "LEFT JOIN", "LEFT OUTER JOIN", "NATURAL JOIN", "ORDER BY",
			"PARTITION BY", "RIGHT JOIN", "RIGHT OUTER JOIN", "START WITH",
			"UNION ALL", "WHEN MATCHED THEN", "WHEN NOT MATCHED THEN",
			"WITH CHECK OPTION", "WITH READ ONLY" };

	// 特殊値
	public static final String[] SPECIAL_VALUE = { "NULL", "SYSDATE" };

	// 記号
	public static final String[] SYMBOL = { "(", ")", "||", ".", ",", "::" , "~~" };
	public static final char[] SYMBOL_CHAR;

	// 演算子
	public static final String[] OPERATOR = { "!=", "*", "+", "-", "/", "<",
			"<=", "<>", "=", ">", ">=", "^=" };
	public static final char[] OPERATOR_CHAR;

	// バインド変数
	public static final char[] BIND_VARIABLE = { ':', '?' };
	//public static final char[] BIND_VARIABLE = { ':', '?' , '~' };	

	// コメント
	public static final String[] COMMENT = { "--", "/*", "*/" };

	// 単語区切り
	public static final char[] WORD_SEPARATE = { ' ', '\t' };

	// SQL区切り
	public static final char[] SQL_SEPARATE = { '/', ';' };

	static {
		// 文字登録
		SYMBOL_CHAR = getCharTable(SYMBOL).toCharArray();
		OPERATOR_CHAR = getCharTable(OPERATOR).toCharArray();
		NEW_LINE_CHAR = getCharTable(NEW_LINES).toCharArray();

		// ソート処理
		Arrays.sort(KEYWORD);
		Arrays.sort(KEYWORD_FUNCTION);
		Arrays.sort(KEYWORD_DATATYPE);
		Arrays.sort(BEGIN_SQL_KEYWORD);
		Arrays.sort(MULTI_KEYWORD);
		Arrays.sort(SPECIAL_VALUE);
		Arrays.sort(SYMBOL);
		Arrays.sort(SYMBOL_CHAR);
		Arrays.sort(BIND_VARIABLE);
		Arrays.sort(OPERATOR);
		Arrays.sort(OPERATOR_CHAR);
		Arrays.sort(COMMENT);
		Arrays.sort(WORD_SEPARATE);
		Arrays.sort(SQL_SEPARATE);
		Arrays.sort(NEW_LINE_CHAR);

		// 改行コード正規表現
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < NEW_LINES.length; i++) {
			if (i != 0)
				sb.append('|');
			sb.append(NEW_LINES[i]);
		}
		NEW_LINES_REGEX = sb.toString();
	};

	/**
	 * 使用されている文字の一覧を返します。
	 *
	 * @param strs
	 * @return
	 */
	private static String getCharTable(String[] strs) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			String str = strs[i];
			int len = strs[i].length();
			for (int j = 0; j < len; j++) {
				char c = str.charAt(j);
				if (sb.indexOf(Character.toString(c)) == -1)
					sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 開始SQLキーワードであるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isBeginSqlKeyword(String str) {
		return (Arrays.binarySearch(BEGIN_SQL_KEYWORD, str) >= 0);
	}

	/**
	 * 複合キーワードであるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isMultiKeyword(String str) {
		return (Arrays.binarySearch(MULTI_KEYWORD, str) >= 0);
	}

	/**
	 * 特殊値であるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isSpecialValue(String str) {
		return (Arrays.binarySearch(SPECIAL_VALUE, str) >= 0);
	}

	/**
	 * シンボルであるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isSymbol(String str) {
		return (Arrays.binarySearch(SYMBOL, str) >= 0);
	}

	/**
	 * バインド変数文字であるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isBindVariable(char c) {
		return (Arrays.binarySearch(BIND_VARIABLE, c) >= 0);
	}

	/**
	 * 値であるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isValue(String str) {
		if (str == null)
			return false;
		return (str.startsWith("\"") && str.endsWith("\""));
	}

	/**
	 * コメントであるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isComment(String str) {
		return (Arrays.binarySearch(COMMENT, str) >= 0);
	}

	/**
	 * 単語区切り文字であるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isWordSeparate(char c) {
		return (Arrays.binarySearch(WORD_SEPARATE, c) >= 0);
	}

	/**
	 * SQL区切り文字であるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isSqlSeparate(char c) {
		return (Arrays.binarySearch(SQL_SEPARATE, c) >= 0);
	}

	/**
	 * 名称に使用可能である文字かを返します。
	 *
	 * @param c
	 * @return
	 */
	public static boolean isNameChar(char c) {
		if (Character.isLetterOrDigit(c))
			return true;
		return (c == '_' || c == '$' || c == '#');
	}

	/**
	 * 数値に使用可能である文字かを返します。
	 *
	 * @param c
	 * @return
	 */
	public static boolean isNumberChar(char c) {
		if (Character.isDigit(c))
			return true;
		switch (c) {
		case '.':
		case '+':
		case '-':
		case 'd':
		case 'D':
		case 'e':
		case 'E':
		case 'f':
		case 'F':
			return true;

		default:
			return false;
		}
	}

	/**
	 * 記号に使用可能である文字かを返します。
	 *
	 * @param c
	 * @return
	 */
	public static boolean isSymbolChar(char c) {
		return (Arrays.binarySearch(SYMBOL_CHAR, c) >= 0);
	}

	/**
	 * 指定された文字が演算子文字列であるかを返します。
	 *
	 * @param str
	 * @return
	 */
	public static boolean isOperator(String str) {
		return (Arrays.binarySearch(OPERATOR, str) >= 0);
	}

	/**
	 * 指定された文字が演算子であるかを返します。
	 *
	 * @param c
	 * @return
	 */
	public static boolean isOperatorChar(char c) {
		return (Arrays.binarySearch(OPERATOR_CHAR, c) >= 0);
	}

	/**
	 * タイプ文字列を返します。（デバッグ用）
	 *
	 * @param type
	 * @return
	 */
	public static String debugTypeString(int type) {
		String str = "";
		switch (type) {
		case TYPE_BEGIN_SQL:
			str = "開始SQL";
			break;
		case TYPE_COMMENT:
			str = "コメント";
			break;
		case TYPE_EMPTY_LINE:
			str = "空行";
			break;
		case TYPE_END_SQL:
			str = "終了SQL";
			break;
		case TYPE_KEYWORD:
			str = "キーワード";
			break;
		case TYPE_NAME:
			str = "名称";
			break;
		case TYPE_NEW_LINE:
			str = "改行";
			break;
		case TYPE_OPERATOR:
			str = "演算子";
			break;
		case TYPE_SQL_SEPARATE:
			str = "SQL区切り文字";
			break;
		case TYPE_SYMBOL:
			str = "記号";
			break;
		case TYPE_VALUE:
			str = "値";
			break;
		case TYPE_UNKNOWN:
			str = "不明";
			break;
		}
		return "『" + str + "』";
	}

	/**
	 * サブタイプ文字列を返します。（デバッグ用）
	 *
	 * @param subType
	 * @return
	 */
	public static String debugSubTypeString(int subType) {
		String str = "";
		switch (subType) {
		case SUBTYPE_COMMENT_MULTI:
			str = "複数行";
			break;
		case SUBTYPE_COMMENT_SINGLE:
			str = "一行";
			break;
		case SUBTYPE_DEFAULT:
			str = "";
			break;
		case SUBTYPE_KEYWORD_DATATYPE:
			str = "データ型";
			break;
		case SUBTYPE_KEYWORD_FUNCTION:
			str = "関数";
			break;
		case SUBTYPE_VALUE_BIND:
			str = "バインド変数";
			break;
		case SUBTYPE_VALUE_NUMERIC:
			str = "数値";
			break;
		case SUBTYPE_VALUE_STRING:
			str = "文字値";
			break;
		}
		return "『" + str + "』";
	}

	/**
	 * デバッグ用文字列を返します。
	 *
	 * @param str
	 * @return
	 */
	public static String debugString(String str) {
		String debugString = str;
		for (int i = 0; i < NEW_LINES.length; i++) {
			debugString = debugString.replaceAll(NEW_LINES[i],
					DEBUG_NEW_LINES[i]);
		}
		return "『" + debugString + "』";
	}

	/**
	 * 改行文字であるかを返します。
	 *
	 * @param c
	 * @return
	 */
	public static boolean isNewLineChar(char c) {
		return (Arrays.binarySearch(NEW_LINE_CHAR, c) >= 0);
	}

	/**
	 * 有効SQLトークンであるかを返します。
	 *
	 * @param token
	 * @return
	 */
	public static boolean isValidToken(Token token) {
		if (token == null)
			return false;

		switch (token.getType()) {
		case TYPE_KEYWORD:
		case TYPE_NAME:
		case TYPE_OPERATOR:
		case TYPE_SYMBOL:
		case TYPE_VALUE:
		case TYPE_SQL_SEPARATE:
			return true;
		default:
			return false;
		}
	}
}
