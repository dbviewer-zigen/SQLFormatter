/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.format;

import java.util.Arrays;

import zigen.sql.tokenizer.TokenUtil;
import zigen.sql.util.ArrayUtil;
import zigen.sql.util.StringUtil;

public class SqlFormatRule implements ISqlFormatRule {

	/**
	 * 設定項目
	 */
	// キーワード変換
	private int convertKeyword = CONVERT_STRING_UPPERCASE;

	// 名前変換
	private int convertName = CONVERT_STRING_NONE;

	// インデント文字
	private String indentString = "    ";

	// 出力改行文字変換
	private int outNewLineCode = NEWLINE_CODE_SYSTEM;

	// 出力改行文字コード
	private String outNewLineCodeStr = System.getProperty("line.separator");

	// 出力改行コード末尾文字
	private char outNewLineEnd = System.getProperty("line.separator").charAt(
			System.getProperty("line.separator").length() - 1);

	// 出力SQL区切り文字変換
	private int outSqlSeparator = SQL_SEPARATOR_SLASH;

	// 出力SQL区切り文字
	private char outSqlSeparatorChar = '/';

	// 改行条件 『,』前に改行するか
	private boolean newLineBeforeComma = true;

	// 改行条件 『AND OR』前に改行するか
	private boolean newLineBeforeAndOr = true;

	// 改行条件 データ型の()を改行するか
	private boolean newLineDataTypeParen = false;

	// 改行条件 関数の()を改行するか
	private boolean newLineFunctionParen = false;

	// DECODE特殊フォーマット CASE文ライクに改行
	private boolean decodeSpecialFormat = true;

	// IN特殊フォーマット 括弧内が値のみの場合は、改行しない
	private boolean inSpecialFormat = true;

	// BETWEEN特殊フォーマット BETWEEN句のANDを改行し、インデントを下げる。
	private boolean betweenSpecialFormat = false;

	// コメント削除するか
	private boolean removeComment = false;

	// 空白行削除するか
	private boolean removeEmptyLine = false;

	// 空白行をインデントするか
	private boolean indentEmptyLine = false;

	// 行折り返しするか
	private boolean wordBreak = false;

	// 行幅指定
	private int width = 80;

	// 関数
	private String[] functions = TokenUtil.KEYWORD_FUNCTION;

	// データ型
	private String[] dataTypes = TokenUtil.KEYWORD_DATATYPE;

	/**
	 * コンストラクタ
	 */
	public SqlFormatRule() {
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#getConvertKeyword()
	 */
	public int getConvertKeyword() {
		return convertKeyword;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setConvertKeyword(int)
	 */
	public void setConvertKeyword(int convertKeyword) {
		this.convertKeyword = convertKeyword;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#getConvertName()
	 */
	public int getConvertName() {
		return convertName;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setConvertName(int)
	 */
	public void setConvertName(int convertName) {
		this.convertName = convertName;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#getIndentString()
	 */
	public String getIndentString() {
		return indentString;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setIndentString(java.lang.String)
	 */
	public void setIndentString(String indentString) {
		this.indentString = (indentString == null) ? "" : indentString;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#isNewLineBeforeComma()
	 */
	public boolean isNewLineBeforeComma() {
		return newLineBeforeComma;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setNewLineBeforeComma(boolean)
	 */
	public void setNewLineBeforeComma(boolean newLineBeforeComma) {
		this.newLineBeforeComma = newLineBeforeComma;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#isNewLineDataTypeParen()
	 */
	public boolean isNewLineDataTypeParen() {
		return newLineDataTypeParen;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setNewLineDataTypeParen(boolean)
	 */
	public void setNewLineDataTypeParen(boolean newLineDataTypeParen) {
		this.newLineDataTypeParen = newLineDataTypeParen;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#isNewLineFunctionParen()
	 */
	public boolean isNewLineFunctionParen() {
		return newLineFunctionParen;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setNewLineFunctionParen(boolean)
	 */
	public void setNewLineFunctionParen(boolean newLineFunctionParen) {
		this.newLineFunctionParen = newLineFunctionParen;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#isRemoveComment()
	 */
	public boolean isRemoveComment() {
		return removeComment;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setRemoveComment(boolean)
	 */
	public void setRemoveComment(boolean removeComment) {
		this.removeComment = removeComment;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#isRemoveEmptyLine()
	 */
	public boolean isRemoveEmptyLine() {
		return removeEmptyLine;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setRemoveEmptyLine(boolean)
	 */
	public void setRemoveEmptyLine(boolean removeEmptyLine) {
		this.removeEmptyLine = removeEmptyLine;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#isIndentEmptyLine()
	 */
	public boolean isIndentEmptyLine() {
		return indentEmptyLine;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setIndentEmptyLine(boolean)
	 */
	public void setIndentEmptyLine(boolean indentEmptyLine) {
		this.indentEmptyLine = indentEmptyLine;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#isWordBreak()
	 */
	public boolean isWordBreak() {
		return wordBreak;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setWordBreak(boolean)
	 */
	public void setWordBreak(boolean wordBreak) {
		this.wordBreak = wordBreak;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#getWidth()
	 */
	public int getWidth() {
		return width;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setWidth(int)
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#getOutNewLineCodeStr()
	 */
	public String getOutNewLineCodeStr() {
		return outNewLineCodeStr;
	}

	/**
	 * @return the outNewLineEnd
	 */
	public char getOutNewLineEnd() {
		return outNewLineEnd;
	}

	/**
	 * @return the outSqlSeparatorChar
	 */
	public char getOutSqlSeparatorChar() {
		return outSqlSeparatorChar;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#isNewLineBeforeAndOr()
	 */
	public boolean isNewLineBeforeAndOr() {
		return newLineBeforeAndOr;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setNewLineBeforeAndOr(boolean)
	 */
	public void setNewLineBeforeAndOr(boolean newLineBeforeAndOr) {
		this.newLineBeforeAndOr = newLineBeforeAndOr;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#getFunctions()
	 */
	public String[] getFunctions() {
		return functions;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#getDataTypes()
	 */
	public String[] getDataTypes() {
		return dataTypes;
	}

	/**
	 * @return the decodeSpecialFormat
	 */
	public boolean isDecodeSpecialFormat() {
		return decodeSpecialFormat;
	}

	/**
	 * @param decodeSpecialFormat
	 *            the decodeSpecialFormat to set
	 */
	public void setDecodeSpecialFormat(boolean decodeSpecialFormat) {
		this.decodeSpecialFormat = decodeSpecialFormat;
	}

	/**
	 * @return the inSpecialFormat
	 */
	public boolean isInSpecialFormat() {
		return inSpecialFormat;
	}

	/**
	 * @param inSpecialFormat
	 *            the inSpecialFormat to set
	 */
	public void setInSpecialFormat(boolean inSpecialFormat) {
		this.inSpecialFormat = inSpecialFormat;
	}

	/**
	 * @return the betweenSpecialFormat
	 */
	public boolean isBetweenSpecialFormat() {
		return betweenSpecialFormat;
	}

	/**
	 * @param betweenSpecialFormat
	 *            the betweenSpecialFormat to set
	 */
	public void setBetweenSpecialFormat(boolean betweenSpecialFormat) {
		this.betweenSpecialFormat = betweenSpecialFormat;
	}

	/**
	 * @return the outSqlSeparator
	 */
	public int getOutSqlSeparator() {
		return outSqlSeparator;
	}

	/**
	 * @param outSqlSeparator
	 *            the outSqlSeparator to set
	 */
	public void setOutSqlSeparator(int outSqlSeparator) {
		this.outSqlSeparator = outSqlSeparator;

		switch (this.outSqlSeparator) {
		case SQL_SEPARATOR_NONE:
			break;

		case SQL_SEPARATOR_SLASH:
			this.outSqlSeparatorChar = '/';
			break;

		case SQL_SEPARATOR_SEMICOLON:
			this.outSqlSeparatorChar = ';';
			break;

		default:
			break;
		}
	}

	/**
	 * 関数キーワードを設定します。
	 * 
	 * @param functions
	 */
	public void setFunctions(String[] functions) {
		functions = StringUtil.toUpperCase(functions); // 大文字変換
		this.functions = functions;
		Arrays.sort(this.functions); // ソート処理
	}

	/**
	 * データ型キーワードを設定します。
	 * 
	 * @param dataTypes
	 */
	public void setDataTypes(String[] dataTypes) {
		dataTypes = StringUtil.toUpperCase(dataTypes); // 大文字変換
		this.dataTypes = dataTypes;
		Arrays.sort(this.dataTypes); // ソート処理
	}

	/**
	 * 指定された関数キーワードを追加します。
	 * 
	 * @param functions
	 */
	public void addFunctions(String[] functions) {
		functions = StringUtil.toUpperCase(functions); // 大文字変換
		this.functions = (String[]) ArrayUtil.add(this.functions, functions,
				new String[0]);
		Arrays.sort(this.functions); // ソート処理
	}

	/**
	 * 指定された関数キーワードを追加します。
	 * 
	 * @param dataTypes
	 */
	public void addDataTypes(String[] dataTypes) {
		dataTypes = StringUtil.toUpperCase(dataTypes); // 大文字変換
		this.dataTypes = (String[]) ArrayUtil.add(this.dataTypes, dataTypes,
				new String[0]);
		Arrays.sort(this.dataTypes); // ソート処理
	}

	/**
	 * 指定された関数キーワードを削除します。
	 * 
	 * @param functions
	 */
	public void subtractFunctions(String[] functions) {
		functions = StringUtil.toUpperCase(functions); // 大文字変換
		this.functions = (String[]) ArrayUtil.subtract(this.functions,
				functions, new String[0]);
	}

	/**
	 * 指定されたデータ型キーワードを削除します。
	 * 
	 * @param dataTypes
	 */
	public void subtractDataTypes(String[] dataTypes) {
		dataTypes = StringUtil.toUpperCase(dataTypes); // 大文字変換
		this.dataTypes = (String[]) ArrayUtil.subtract(this.dataTypes,
				dataTypes, new String[0]);
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#getOutNewLineCode()
	 */
	public int getOutNewLineCode() {
		return outNewLineCode;
	}

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormatRule#setOutNewLineCode(int)
	 */
	public void setOutNewLineCode(int outNewLineCode) {
		this.outNewLineCode = outNewLineCode;

		// 改行文字を設定
		switch (outNewLineCode) {
		case NEWLINE_CODE_SYSTEM:
			this.outNewLineCodeStr = System.getProperty("line.separator");
			break;

		case NEWLINE_CODE_CRLF:
		case NEWLINE_CODE_CR:
		case NEWLINE_CODE_LF:
			this.outNewLineCodeStr = TokenUtil.NEW_LINES[outNewLineCode - 1];
			break;
		}

		// 改行コード末尾文字設定
		this.outNewLineEnd = this.outNewLineCodeStr
				.charAt(this.outNewLineCodeStr.length() - 1);
	}

	/**
	 * キーワードであるかを返します。
	 * 
	 * @param str
	 * @return
	 */
	public boolean isKeyword(String str) {
		return (Arrays.binarySearch(TokenUtil.KEYWORD, str) >= 0)
				|| (Arrays.binarySearch(this.functions, str) >= 0)
				|| (Arrays.binarySearch(this.dataTypes, str) >= 0);
	}

	/**
	 * 指定された文字列が関数キーワードであるかを返します。
	 * 
	 * @param str
	 * @return
	 */
	public boolean isFunctions(String str) {
		return (Arrays.binarySearch(this.functions, str) >= 0);
	}

	/**
	 * 指定された文字列がデータ型キーワードであるかを返します。
	 * 
	 * @param str
	 * @return
	 */
	public boolean isDataTypes(String str) {
		return (Arrays.binarySearch(this.dataTypes, str) >= 0);
	}

	/**
	 * 名称であるかを返します。
	 * 
	 * @param str
	 * @return
	 */
	public boolean isName(String str) {
		boolean b = isKeyword(str);
		b |= TokenUtil.isSymbol(str);
		b |= TokenUtil.isValue(str);
		b |= TokenUtil.isComment(str);
		b |= TokenUtil.isSqlSeparate(str.charAt(0));
		return !b;
	}
}
