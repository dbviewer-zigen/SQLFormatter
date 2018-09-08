/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.format;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import zigen.sql.tokenizer.SqlTokenizer;
import zigen.sql.tokenizer.Token;
import zigen.sql.tokenizer.TokenUtil;
import zigen.sql.util.StringUtil;

public class SqlFormat implements ISqlFormat {

	/**
	 * 設定項目
	 */
	// フォーマットルール
	private SqlFormatRule rule;

	/**
	 * 内部変数
	 */
	// トークン分割
	private SqlTokenizer tokenizer;

	/**
	 * コンストラクタ
	 */
	public SqlFormat(ISqlFormatRule rule) {
		this.rule = (SqlFormatRule) rule;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see kry.sql.format.ISqlFormat#format(java.lang.String)
	 */
	public String format(String sql) throws SqlFormatException {
		return format(sql, 0);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see kry.sql.format.ISqlFormat#format(java.lang.String, int)
	 */
	public String format(String sql, int offset) throws SqlFormatException {
		try {
			return innerFormat(sql, offset);

		} catch (Exception e) {
			throw new SqlFormatException(e.getMessage());
		}
	}

	/**
	 * アンフォーマット処理（改行無し）
	 *
	 * @param sql
	 * @return
	 */
	public String unFormat(String sql) {
		tokenizer = new SqlTokenizer(sql, rule);

		StringBuffer sb = new StringBuffer();
		int beforeType = TokenUtil.TYPE_BEGIN_SQL;

		for (Iterator it = tokenizer; it.hasNext();) {
			Token token = (Token) it.next();
			int type = token.getType();
			int subType = token.getSubType();
			String upper = token.getUpper();
			int len = sb.length();
			char lc = (len == 0) ? (char) -1 : sb.charAt(len - 1);
			boolean insertSpace = true;

			// 文字列前スペース挿入条件
			switch (type) {
			case TokenUtil.TYPE_SYMBOL:
				if ("(".equals(upper)) {
					Token parentTokenInParen = token.getParentTokenInParen();
					if (parentTokenInParen != null) {
						int parentType = parentTokenInParen.getType();
						int parentSubType = parentTokenInParen.getSubType();
						if (parentSubType == TokenUtil.SUBTYPE_KEYWORD_DATATYPE
								|| parentSubType == TokenUtil.SUBTYPE_KEYWORD_FUNCTION
								|| parentType == TokenUtil.TYPE_NAME) {
							insertSpace = false;
						}
					}

				} else if (")".equals(upper) || "(*)".equals(upper)
						|| "(+)".equals(upper))
					insertSpace = false;
				break;

			case TokenUtil.TYPE_COMMENT:
				if (rule.isRemoveComment()) // コメント削除:true/false
					insertSpace = false;
				break;

			case TokenUtil.TYPE_NEW_LINE:
			case TokenUtil.TYPE_EMPTY_LINE:
			case TokenUtil.TYPE_SQL_SEPARATE:
				insertSpace = false;
				break;

			default:
				insertSpace = true;
				break;
			}

			if (insertSpace) {
				if (lc == '(')
					;
				else if (rule.isNewLineBeforeComma() && ",".equals(upper))
					;
				else if (!rule.isNewLineBeforeComma() && lc == ',')
					;
				else if (beforeType == TokenUtil.TYPE_BEGIN_SQL
						|| beforeType == TokenUtil.TYPE_SQL_SEPARATE)
					;
				else
					sb.append(' ');
			}

			// 文字追加条件
			switch (type) {
			case TokenUtil.TYPE_NEW_LINE:
			case TokenUtil.TYPE_EMPTY_LINE:
				break;

			case TokenUtil.TYPE_COMMENT:
				if (rule.isRemoveComment()) // コメント削除:true/false
					break;

			default:
				// 文字変換
				sb.append(convertString(token, 0, false));

				if (type == TokenUtil.TYPE_COMMENT
						&& subType == TokenUtil.SUBTYPE_COMMENT_SINGLE) { // 一行コメントは改行する。
					sb.append(rule.getOutNewLineCodeStr());
				}
			}

			// タイプ記憶
			beforeType = type;
		}

		return sb.toString().trim();
	}

	/**
	 * アンフォーマット（インデントなし）
	 *
	 * @param sql
	 * @return
	 */
	public String unFormat2(String sql) throws SqlFormatException {
		try {
			String indentString = rule.getIndentString();
			rule.setIndentString("");

			String unFormat = innerFormat(sql, 0);
			rule.setIndentString(indentString);

			return unFormat;
		} catch (Exception e) {
			throw new SqlFormatException(e.getMessage());
		}
	}

	/**
	 * フォーマット処理
	 *
	 * @param sql
	 * @param offset
	 * @return
	 * @throws Exception
	 */
	private String innerFormat(String sql, int offset) throws Exception {
		tokenizer = new SqlTokenizer(sql, this.rule);

		// インデント量計算
		int initIndent = (offset == 0) ? 0 : (offset - 1)
				/ rule.getIndentString().length() + 1;

		StringBuffer sb = new StringBuffer();
		int indent = initIndent;
		boolean isBetween = false; // Between句
		boolean isOnUsing = false; // On句 または Using句
		boolean isTrim = false; // Trim句
		Token sqlToken = null; // SQL文
		Stack parenStack = new Stack(); // 括弧スタック
		List selectList = new ArrayList(); // SELECTリスト

		for (Iterator it = tokenizer; it.hasNext();) {
			Token token = (Token) it.next();
			int type = token.getType();
			String upper = token.getUpper();
			Token parantTokenInParen = token.getParentTokenInParen();
			token.setIndent(indent); // インデント設定

			switch (type) {
			case TokenUtil.TYPE_KEYWORD:
				// SQL文
				if (sqlToken == null) {
					sqlToken = token;
				}

				if ("SELECT".equals(upper)) {
					// SELECT文以外は改行、インデント
					if (!"SELECT".equals(sqlToken.getUpper())
							&& selectList.isEmpty()) {
						newLine(sb);
						indent += 1;
					}
					append(sb, token, indent);
					newLine(sb);
					indent += 2;
					selectList.add(token); // Unionインデント計算用
					// }

				} else if ("UPDATE".equals(upper) || "DELETE".equals(upper)) {

					if (sqlToken == null) {
						sqlToken = token;
					}

					if ("CREATE".equals(sqlToken.getUpper())) {
						append(sb, token, indent);

					} else {
						append(sb, token, indent);
						newLine(sb);
						indent += 2;
					}

				} else if ("FROM".endsWith(upper) || "WHERE".equals(upper)
						|| "HAVING".equals(upper) || "ORDER BY".equals(upper)
						|| "GROUP BY".equals(upper) || "SET".equals(upper)
						|| "VALUES".equals(upper)
						|| "WITH CHECK OPTION".equals(upper)
						|| "WITH READ ONLY".equals(upper)) {

					if (isTrim && "FROM".endsWith(upper)
							&& parantTokenInParen != null
							&& "TRIM".equals(parantTokenInParen.getUpper())) {
						append(sb, token, indent);
						isTrim = false;
						break;
					}

					newLine(sb);

					if (isOnUsing) {
						indent -= 3;
						isOnUsing = false;
					} else {
						indent -= 1;
					}

					append(sb, token, indent);
					newLine(sb);
					indent += 1;

				} else if ("AND".equals(upper) || "OR".equals(upper)) {

					if (isBetween && "AND".equals(upper)) {
						if (rule.isBetweenSpecialFormat()) {
							newLine(sb);
							append(sb, token, indent + 1);
							token.setIndent(indent + 1);
						} else {
							append(sb, token, indent);
						}
						isBetween = false;

					} else if (!rule.isNewLineBeforeAndOr()) {
						append(sb, token, indent);
						newLine(sb);

					} else {
						newLine(sb);
						append(sb, token, indent);
					}

				} else if ("WHEN".equals(upper) || "ELSE".equals(upper)
						|| "INCREMENT BY".equals(upper)
						|| "START WITH".equals(upper)
						|| "MAXVALUE".equals(upper)
						|| "NOMAXVALUE".equals(upper)
						|| "MINVALUE".equals(upper)
						|| "NOMINVALUE".equals(upper) || "CYCLE".equals(upper)
						|| "NOCYCLE".equals(upper) || "CACHE".equals(upper)) {
					// NL、WORD
					newLine(sb);
					append(sb, token, indent);

				} else if ("CREATE".equals(upper) || "INSERT".equals(upper)
						|| "INTO".equals(upper) || "DROP".equals(upper)
						|| "TRUNCATE".equals(upper)
						// || "COLUMN".equals(upper)
						|| "MERGE".equals(upper) || "ALTER".equals(upper)
						|| "CREATE OR REPLACE".equals(upper)) {
					// WORD,NL,INC+1
					append(sb, token, indent);
					newLine(sb);
					indent += 1;

				} else if ("END".equals(upper)) {
					// NL,WORD,INC-1
					newLine(sb);
					indent -= 1;
					append(sb, token, indent);

				} else if ("CASE".equals(upper)
						|| "SEQUENCE".equals(upper)
						|| (rule.isDecodeSpecialFormat() && "DECODE"
								.equals(upper))) {
					// WORD,INC+1
					append(sb, token, indent);
					indent += 1;

				} else if ("BETWEEN".equals(upper)) {
					isBetween = true;
					append(sb, token, indent);

				} else if ("USING".equals(upper) || "ON".equals(upper)) {

					if ("CREATE".equals(sqlToken.getUpper())) {
						append(sb, token, indent);

					} else {
						newLine(sb);
						if (!isOnUsing) {
							indent += 1;
						}
						append(sb, token, indent);
						isOnUsing = true;
					}

				} else if ("JOIN".equals(upper) || upper.startsWith("FULL")
						|| upper.startsWith("LEFT")
						|| upper.startsWith("RIGHT")) {
					if (isOnUsing) {
						indent -= 2;
						isOnUsing = false;
					}
					indent += 1;
					newLine(sb);
					append(sb, token, indent);

				} else if ("UNION".equals(upper) || "UNION ALL".equals(upper)
						|| "INTERSECT".equals(upper) || "EXCEPT".equals(upper)
						|| "MINUS".equals(upper)) {
					newLine(sb);
					indent = getUnionIndent(token, indent, selectList); // Unionインデント取得
					append(sb, token, indent);
					newLine(sb);

				} else if ("WHEN MATCHED THEN".equals(upper)
						|| "FOR UPDATE".equals(upper)) {
					isOnUsing = false;
					newLine(sb);
					indent -= 1;
					append(sb, token, indent);
					indent += 1;

				} else if ("WHEN NOT MATCHED THEN".equals(upper)) {
					newLine(sb);
					indent -= 3;
					append(sb, token, indent);
					indent += 1;

				} else if ("TRIM".equals(upper)) {
					isTrim = true;
					append(sb, token, indent);

				} else if ("MODIFY".equals(upper)) {
					// NL,INC+1,WORD
					newLine(sb);
					indent += 1;
					append(sb, token, indent);

				} else if ("GRANT".equals(upper) || "REVOKE".equals(upper)) {
					// WORD,NL,INC+2
					append(sb, token, indent);
					newLine(sb);
					indent += 2;

				} else if ("PARTITION BY".equals(upper)) {
					// NL,INC+1,WORD,NL
					newLine(sb);
					append(sb, token, indent);
					indent += 1;
					newLine(sb);

				} else {
					// WORD
					append(sb, token, indent);

				}
				break;

			case TokenUtil.TYPE_SYMBOL: // 記号
				if (",".equals(upper)) {
					if (isOnUsing) {
						indent -= 2;
						isOnUsing = false;
					}

					if (!checkNewLineInParen(token)) {
						append(sb, token, indent);

					} else if (rule.isNewLineBeforeComma()) {
						newLine(sb);
						append(sb, token, indent);
					} else {
						append(sb, token, indent);
						newLine(sb);
					}

				} else if ("(".equals(upper)) {
					append(sb, token, indent);

					if (checkNewLineInParen(token)) {
						newLine(sb);
						indent += 1;
					}

					parenStack.push(new Integer(indent));

				} else if (")".equals(upper)) {
					try {
						indent = ((Integer) parenStack.pop()).intValue();
					} catch (RuntimeException e) {
						ExceptionHandler.handleException("')'に対する'('がありません。",
								token);
					}

					if (checkNewLineInParen(token)) {
						newLine(sb);
						indent -= 1;
					}

					append(sb, token, indent);

				} else {
					append(sb, token, indent);
				}
				break;

			case TokenUtil.TYPE_NAME:
			case TokenUtil.TYPE_VALUE:
			case TokenUtil.TYPE_OPERATOR:
			case TokenUtil.TYPE_COMMENT:
				append(sb, token, indent);
				break;

			case TokenUtil.TYPE_SQL_SEPARATE:
				indent = initIndent; // インデントクリア
				selectList.clear(); // SELECTリストクリア
				sqlToken = null; // SQL文クリア
				append(sb, token, indent);
				break;

			case TokenUtil.TYPE_EMPTY_LINE:
				if (sb.length() != 0)
					newLine(sb);
				append(sb, token, indent);
				break;

			case TokenUtil.TYPE_UNKNOWN:
				append(sb, token, indent);
				break;

			case TokenUtil.TYPE_NEW_LINE:
			default:
				break;
			}
		}

		return sb.toString().trim();
	}

	/**
	 * 文字を追加します。
	 *
	 * @param sb
	 * @param str
	 * @param indent
	 * @return
	 */
	private StringBuffer append(StringBuffer sb, Token token, int indent) {
		int type = token.getType();

		if (type == TokenUtil.TYPE_COMMENT && rule.isRemoveComment()) // コメント削除:true/false
			return sb;
		else if (type == TokenUtil.TYPE_EMPTY_LINE && rule.isRemoveEmptyLine()) // 空行削除:true/false
			return sb;

		String upper = token.getUpper();
		Token parentToken = token.getParentTokenInParen();
		char c = (sb.length() == 0) ? (char) -1 : sb.charAt(sb.length() - 1);
		boolean isHeadLine = false; // 行の先頭か？

		if (c == (char) -1 || c == rule.getOutNewLineEnd()) {
			isHeadLine = true;

			// 行の開始の場合、インデント
			if (type != TokenUtil.TYPE_EMPTY_LINE || rule.isIndentEmptyLine())
				indent(sb, indent);

		} else {
			// 行の途中
			if (c == ',') {
				// キーワード『IN』専用
				if (rule.isInSpecialFormat() && parentToken != null
						&& "IN".equals(parentToken.getUpper())
						&& token.isValueOnlyInParen()) {
					sb.append(' ');

					// 関数『DECODE』専用
				} else if (rule.isDecodeSpecialFormat() && parentToken != null
						&& "DECODE".equals(parentToken.getUpper())) {
					int elementIndexInParen = token.getElementIndexInParen();
					if (elementIndexInParen % 2 == 1)
						sb.append(' ');

					// 関数キーワード
				} else if (!rule.isNewLineFunctionParen()
						&& parentToken != null
						&& parentToken.getSubType() == TokenUtil.SUBTYPE_KEYWORD_FUNCTION) {
					sb.append(' ');

				} else if (parentToken != null
						&& parentToken.getSubType() == TokenUtil.SUBTYPE_KEYWORD_DATATYPE
						&& !rule.isNewLineDataTypeParen()) {
					sb.append(' ');

				} else
					;

			} else if ("(".equals(upper)) {
				if (c == '(') {
					;

				} else if (TokenUtil.isOperatorChar(c)) {
					sb.append(' ');

				} else if (parentToken != null
						&& (parentToken.getSubType() == TokenUtil.SUBTYPE_KEYWORD_DATATYPE
								|| parentToken.getSubType() == TokenUtil.SUBTYPE_KEYWORD_FUNCTION || parentToken
								.getType() == TokenUtil.TYPE_NAME)) {
					;
				} else {
					sb.append(' ');
				}

			} else if (")".equals(upper) || c == '(' || "(*)".equals(upper)
					|| "(+)".equals(upper) || ",".equals(upper)) {
				;

			} else if (type == TokenUtil.TYPE_SQL_SEPARATE
					|| token.getSubType() == TokenUtil.SUBTYPE_COMMENT_MULTI) {
				;
			} else
				sb.append(' ');
		}

		// 文字変換
		String fixString = convertString(token, indent, isHeadLine);

		// 行折り返し
		if (rule.isWordBreak() && type != TokenUtil.TYPE_COMMENT) {
			int lineLength = getLineLength(sb) + fixString.length();
			if (rule.getWidth() < lineLength) {
				if (rule.getWidth() >= (rule.getIndentString().length()
						* (indent + 1) + fixString.length())) {
					sb.deleteCharAt(sb.length() - 1); // 末尾のスペースを取り除く
					newLine(sb);
					indent(sb, indent + 1);
					token.setIndent(indent + 1);
				}
			}
		}

		sb.append(fixString);

		return sb;
	}

	/**
	 * 改行します。
	 *
	 * @param sb
	 * @return
	 */
	private StringBuffer newLine(StringBuffer sb) {
		int start = sb.length() - rule.getOutNewLineCodeStr().length();
		if (start < 0)
			return sb.append(rule.getOutNewLineCodeStr());

		if (sb.indexOf(rule.getOutNewLineCodeStr(), start) != -1)
			return sb;
		return sb.append(rule.getOutNewLineCodeStr());
	}

	/**
	 * インデントします。
	 *
	 * @param sb
	 * @param indent
	 * @return
	 */
	private StringBuffer indent(StringBuffer sb, int indent) {
		if (indent <= 0)
			return sb;

		while (indent-- > 0) {
			sb.append(rule.getIndentString());
		}
		return sb;
	}

	/**
	 * 文字変換した文字を返します。
	 *
	 * @param token
	 * @param indent
	 * @param isHeadLine
	 * @return
	 */
	private String convertString(Token token, int indent, boolean isHeadLine) {
		String str = "";
		int type = token.getType();

		switch (type) {
		case TokenUtil.TYPE_KEYWORD:
		case TokenUtil.TYPE_NAME:
			int convertType = (type == TokenUtil.TYPE_KEYWORD) ? rule
					.getConvertKeyword() : rule.getConvertName();

			// 文字変換
			switch (convertType) {
			case ISqlFormatRule.CONVERT_STRING_NONE:
				str = token.getCustom();
				break;
			case ISqlFormatRule.CONVERT_STRING_UPPERCASE:
				str = token.getUpper();
				break;
			case ISqlFormatRule.CONVERT_STRING_LOWERCASE:
				str = token.getCustom().toLowerCase();
				break;
			case ISqlFormatRule.CONVERT_STRING_CAPITALCASE:
				if ('\"' == token.getCustom().charAt(0)) {
					str = "\""
							+ StringUtil.toCapitalcase(token.getCustom()
									.substring(1));
				} else {
					str = StringUtil.toCapitalcase(token.getCustom());
				}
				break;
			}
			break;

		case TokenUtil.TYPE_COMMENT:
			if (token.getSubType() == TokenUtil.SUBTYPE_COMMENT_SINGLE) {
				str = token.getCustom() + rule.getOutNewLineCodeStr();
			} else {
				str = formatMultiComment(token.getCustom(), indent, isHeadLine);
			}
			break;

		case TokenUtil.TYPE_EMPTY_LINE:
			str = rule.getOutNewLineCodeStr();
			break;

		case TokenUtil.TYPE_SQL_SEPARATE:
			switch (rule.getOutSqlSeparator()) {
			case ISqlFormatRule.SQL_SEPARATOR_NONE:
				str = token.getCustom();
				break;
			case ISqlFormatRule.SQL_SEPARATOR_SLASH:
				str = token.getCustom().replace(';',
						rule.getOutSqlSeparatorChar());
				break;
			case ISqlFormatRule.SQL_SEPARATOR_SEMICOLON:
				str = token.getCustom().replace('/',
						rule.getOutSqlSeparatorChar());
				break;
			}
			break;

		default:
			str = token.getCustom();
		}

		return str;
	}

	/**
	 * 現在の行の文字数を返します。
	 *
	 * @param sb
	 * @return
	 */
	private int getLineLength(StringBuffer sb) {
		if (sb == null)
			return 0;

		int start = sb.lastIndexOf(rule.getOutNewLineCodeStr());
		if (start == -1)
			return 0;

		return sb.length() - start - rule.getOutNewLineCodeStr().length();
	}

	/**
	 * 改行するかどうか判定します。（括弧専用）
	 *
	 * @param token
	 * @return
	 */
	private boolean checkNewLineInParen(Token token) {
		Token parentToken = token.getParentTokenInParen();
		if (parentToken != null) {
			String parentUpper = parentToken.getUpper();

			// キーワード『DEFAULT』は改行しない。
			if ("DEFAULT".equals(parentUpper))
				return false;

			// 関数『DECODE』専用改行条件 CASE文ライクに改行
			if (rule.isDecodeSpecialFormat() && "DECODE".equals(parentUpper)) {
				if (")".equals(token.getCustom())) {
					return true;
				}
				int elementIndexInParen = token.getElementIndexInParen();
				return (elementIndexInParen % 2 == 1);
			}

			// 関数の括弧
			if (!rule.isNewLineFunctionParen()
					&& parentToken.getSubType() == TokenUtil.SUBTYPE_KEYWORD_FUNCTION)
				return false;

			// データ型の括弧
			if (!rule.isNewLineDataTypeParen()
					&& parentToken.getSubType() == TokenUtil.SUBTYPE_KEYWORD_DATATYPE)
				return false;

			// キーワード『IN』専用改行条件 括弧内が値のみの場合は、改行しない
			if (rule.isInSpecialFormat() && "IN".equals(parentUpper)
					&& token.isValueOnlyInParen())
				return false;
		}

		// 未登録の括弧内の要素が1の場合改行しない
		if (token.getElementLengthInParen() == 1 && token.isValueOnlyInParen())
			return false;

		return true;
	}

	/**
	 * 複数行コメントをフォーマットします。
	 *
	 * @param str
	 * @param indent
	 * @param isHeadLine
	 * @return
	 */
	private String formatMultiComment(String str, int indent, boolean isHeadLine) {
		StringBuffer sb = new StringBuffer();
		if (!isHeadLine) {
			newLine(sb);
			indent(sb, indent);
		}

		// 改行で分割
		String[] strs = str.split(TokenUtil.NEW_LINES_REGEX);
		for (int i = 0; i < strs.length; i++) {
			if (i != 0)
				indent(sb, indent);
			// 先頭からのスペース・タブを取り除く
			sb.append(StringUtil.leftTrim(strs[i], TokenUtil.WORD_SEPARATE));
			newLine(sb);
		}
		return sb.toString();
	}

	/**
	 * UNION用のインデントを取得します。
	 *
	 * @param token
	 * @param indent
	 * @param selectList
	 * @return
	 */
	private int getUnionIndent(Token token, int indent, List selectList) {
		int depthParen = token.getDepthParen();

		if (selectList.size() == 0)
			return indent;

		for (int i = selectList.size() - 1; i >= 0; i--) {
			Token selectToken = (Token) selectList.get(i);
			if (selectToken.getDepthParen() == depthParen)
				return selectToken.getIndent();
		}

		return indent;
	}
}
