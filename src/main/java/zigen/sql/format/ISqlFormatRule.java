/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.format;

public interface ISqlFormatRule {
	// 文字変換ルール
	public static final int CONVERT_STRING_NONE = 0; // なし
	public static final int CONVERT_STRING_UPPERCASE = 1; // 大文字
	public static final int CONVERT_STRING_LOWERCASE = 2; // 小文字
	public static final int CONVERT_STRING_CAPITALCASE = 3; // 頭大文字

	// 改行文字ルール
	public static final int NEWLINE_CODE_SYSTEM = 0; // システム改行
	public static final int NEWLINE_CODE_CRLF = 1; // \r\n
	public static final int NEWLINE_CODE_CR = 2; // \r
	public static final int NEWLINE_CODE_LF = 3; // \n

	// SQL区切り文字変換ルール
	public static final int SQL_SEPARATOR_NONE = 0; // 変換しない
	public static final int SQL_SEPARATOR_SLASH = 1; // '/'
	public static final int SQL_SEPARATOR_SEMICOLON = 2; // ';'

	public abstract int getConvertKeyword();

	public abstract void setConvertKeyword(int convertKeyword);

	public abstract int getConvertName();

	public abstract void setConvertName(int convertName);

	public abstract String getIndentString();

	public abstract void setIndentString(String indentString);

	public abstract boolean isNewLineBeforeComma();

	public abstract void setNewLineBeforeComma(boolean newLineBeforeComma);

	public abstract boolean isNewLineBeforeAndOr();

	public abstract void setNewLineBeforeAndOr(boolean newLineBeforeAndOr);

	public abstract boolean isNewLineDataTypeParen();

	public abstract void setNewLineDataTypeParen(boolean newLineDataTypeParen);

	public abstract boolean isNewLineFunctionParen();

	public abstract void setNewLineFunctionParen(boolean newLineFunctionParen);

	public abstract boolean isDecodeSpecialFormat();

	public abstract void setDecodeSpecialFormat(boolean decodeSpecialFormat);

	public abstract boolean isInSpecialFormat();

	public abstract void setInSpecialFormat(boolean inSpecialFormat);

	public abstract boolean isBetweenSpecialFormat();

	public abstract void setBetweenSpecialFormat(boolean betweenSpecialFormat);

	public abstract boolean isRemoveComment();

	public abstract void setRemoveComment(boolean removeComment);

	public abstract boolean isRemoveEmptyLine();

	public abstract void setRemoveEmptyLine(boolean removeEmptyLine);

	public abstract boolean isIndentEmptyLine();

	public abstract void setIndentEmptyLine(boolean indentEmptyLine);

	public abstract boolean isWordBreak();

	public abstract void setWordBreak(boolean wordBreak);

	public abstract int getWidth();

	public abstract void setWidth(int width);

	public abstract int getOutNewLineCode();

	public abstract void setOutNewLineCode(int outNewLineCode);

	public abstract int getOutSqlSeparator();

	public abstract void setOutSqlSeparator(int outSqlSeparator);

	public abstract String[] getFunctions();

	public abstract void setFunctions(String[] functions);

	public abstract void addFunctions(String[] functions);

	public abstract void subtractFunctions(String[] functions);

	public abstract String[] getDataTypes();

	public abstract void setDataTypes(String[] dataTypes);

	public abstract void addDataTypes(String[] dataTypes);

	public abstract void subtractDataTypes(String[] dataTypes);

}