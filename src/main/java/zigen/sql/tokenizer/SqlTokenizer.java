/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.tokenizer;

import java.util.Iterator;

import zigen.sql.format.SqlFormatRule;
import zigen.sql.util.StringUtil;

public class SqlTokenizer implements Iterator {
	// スキャナ
	protected SqlScanner scanner;

	// フォーマットルール
	protected SqlFormatRule rule;

	// トークンリスト
	protected TokenList tokenList;
	protected Iterator it;

	// プッシュバック
	private boolean pushedBack = false;
	private Object token;

	/**
	 * コンストラクタ
	 *
	 * @param sql
	 * @param rule
	 */
	public SqlTokenizer(String sql, SqlFormatRule rule) {
		init(sql, rule);
	}

	/**
	 * コンストラクタ
	 * parse()やoptimize()処理で外部からキャンセルできるように、デフォルトコンストラクタを追加します。(ZIGEN)
	 */
	public SqlTokenizer() {}

	/**
	 * parse()やoptimize()処理で外部からキャンセルできるように、init()めデフォルトコンストラクタを追加します。(ZIGEN)
	 * @param sql
	 * @param rule
	 */
	public void init(String sql, SqlFormatRule rule) {
		this.scanner = new SqlScanner(sql);
		this.rule = rule;
		this.tokenList = new TokenList();

		// long start = System.currentTimeMillis();
		// パース
		parse();
		// 最適化
		optimize();
		// System.out.println(System.currentTimeMillis() - start);
		it = this.tokenList.iterator();
	}



	/*
	 * (非 Javadoc)
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (this.pushedBack)
			return true;

		return it.hasNext();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
		if (this.pushedBack) {
			this.pushedBack = false;
			return this.token;
		}
		if (it.hasNext()) {
			this.token = it.next();
			return this.token;
		} else {
			return null;
		}
	}

	/**
	 * プッシュバックします。
	 */
	public void pushBack() {
		if (token != null) {
			pushedBack = true;
		}
	}

	/**
	 * 処理を中断させる場合はtrueを返します。
	 * @return
	 */
	protected boolean isCanceled(){
		return false;
	}

	/**
	 * sqlをパースします。
	 */
	protected void parse() {
		// SQL開始トークン
		Token token = new Token("", 0, 0, 0);
		token.setType(TokenUtil.TYPE_BEGIN_SQL);
		this.tokenList.add(token);
		int x = 0;
		int y = 0;
		int beforeType = TokenUtil.TYPE_UNKNOWN;
		int depthParen = 0;

		for (; scanner.hasNext();) {
			if(isCanceled()) return;

			int skipCount = scanner.skipSpaceTab();
			if (!scanner.hasNext())
				return;

			x += skipCount; // X座標計算
			int incY = 0; // Y座標増分

			StringBuffer sb = new StringBuffer();

			int index = scanner.getCurrent(); // 先頭からの位置
			int type = TokenUtil.TYPE_UNKNOWN;
			int subType = 0;

			char c = scanner.peek();
			if (c == '\"') { // "XXXXX"
				// 名称
				do {
					sb.append(scanner.next());
					c = scanner.peek();
				} while (c != '\"' && scanner.hasNext());

				if (scanner.hasNext())
					sb.append(scanner.next());
				type = TokenUtil.TYPE_NAME;

			} else if (c == '\'') { // 'XXXXX'
				// 値
				sb.append(scanner.next());
				c = scanner.peek();

				do {
					if (scanner.isPeekEquals("''")) {
						sb.append(scanner.next());
						sb.append(scanner.next());
						c = scanner.peek();

					} else if (c == '\'' && scanner.peek(1) != '\'') {
						break;

					} else {
						sb.append(scanner.next());
						c = scanner.peek();
					}

				} while ((c != '\'' && scanner.hasNext())
						|| (scanner.isPeekEquals("''")));

				if (scanner.hasNext())
					sb.append(scanner.next());
				type = TokenUtil.TYPE_VALUE;
				subType = TokenUtil.SUBTYPE_VALUE_STRING;
			} else if (c == '`') { // 'XXXXX'
				// 値
				sb.append(scanner.next());
				c = scanner.peek();

				do {
					if (scanner.isPeekEquals("''")) {
						sb.append(scanner.next());
						sb.append(scanner.next());
						c = scanner.peek();

					} else if (c == '`' && scanner.peek(1) != '\'') {
						break;

					} else {
						sb.append(scanner.next());
						c = scanner.peek();
					}

				} while ((c != '`' && scanner.hasNext())
						|| (scanner.isPeekEquals("''")));

				if (scanner.hasNext())
					sb.append(scanner.next());
				type = TokenUtil.TYPE_VALUE;
				subType = TokenUtil.SUBTYPE_VALUE_STRING;

			} else if (scanner.isPeekEquals("--")) {
				// １行コメント
				sb.append(scanner.next());
				c = scanner.peek();

				do {
					sb.append(scanner.next());
					c = scanner.peek();
				} while (scanner.hasNext()
						&& !scanner.isPeekEquals(TokenUtil.NEW_LINES));

				type = TokenUtil.TYPE_COMMENT;
				subType = TokenUtil.SUBTYPE_COMMENT_SINGLE;

			} else if (scanner.isPeekEquals("/*")) {
				// 複数行コメント
				sb.append(scanner.next());
				c = scanner.peek();

				do {
					sb.append(scanner.next());
					c = scanner.peek();

					if (scanner.isPeekEquals(TokenUtil.NEW_LINES)) {
						if (scanner.isPeekEquals("\r\n")) {
							sb.append(scanner.next());
						}
						incY++;
					}

				} while (!scanner.isPeekEquals("*/") && scanner.hasNext());
				if (scanner.hasNext()) {
					sb.append(scanner.next());
					sb.append(scanner.next());
				}
				type = TokenUtil.TYPE_COMMENT;
				subType = TokenUtil.SUBTYPE_COMMENT_MULTI;

			} else if (scanner.isPeekNextEqualsEx("(*)")) {
				sb.append("(*)");
				type = TokenUtil.TYPE_SYMBOL;

			} else if (scanner.isPeekNextEqualsEx("(+)")) {
				sb.append("(+)");
				type = TokenUtil.TYPE_SYMBOL;

			} else if (scanner.isPeekEquals(TokenUtil.NEW_LINES)) {

				// 空行か？
				if (beforeType == TokenUtil.TYPE_NEW_LINE
						|| beforeType == TokenUtil.TYPE_EMPTY_LINE) {
					type = TokenUtil.TYPE_EMPTY_LINE;
				} else {
					type = TokenUtil.TYPE_NEW_LINE;
				}
				index = scanner.getCurrent() - skipCount;

				// 改行
				if (scanner.isPeekEquals("\r\n")) {
					sb.append(scanner.next());
				}
				sb.append(scanner.next());
				incY++; // Y座標

			} else if (Character.isDigit(c)
					|| ((c == '.' || c == '+' || c == '-') && Character
							.isDigit(scanner.peek(1)))) {
				// 数値 ~[0-9].*|^[+-.][0-9].*
				do {
					sb.append(c);
					scanner.next();
					c = scanner.peek();
				} while (TokenUtil.isNumberChar(c));
				type = TokenUtil.TYPE_VALUE;
				subType = TokenUtil.SUBTYPE_VALUE_NUMERIC;

			} else if (TokenUtil.isOperatorChar(c)) {
				// 演算子
				String str = scanner
						.getPeekNextEqualsExString(TokenUtil.OPERATOR);

				if (str != null) {
					sb.append(str);
					if ("(".equals(str)) {
						depthParen++;
					}

				} else {
					// （不正な）演算子文字列
					sb.append(scanner.next());
				}
				type = TokenUtil.TYPE_OPERATOR;

			} else if (TokenUtil.isBindVariable(c)
					&& !scanner.isPeekEqualsEx("::")) {
				// バインド変数
				sb.append(scanner.next());
				type = TokenUtil.TYPE_VALUE;
				subType = TokenUtil.SUBTYPE_VALUE_BIND;

			} else if (TokenUtil.isSymbolChar(c)) {
				// 記号
				String str = scanner
						.getPeekNextEqualsExString(TokenUtil.SYMBOL);

				if (str != null) {
					sb.append(str);
					if ("(".equals(str)) {
						depthParen++;
					}

				} else {
					// （不正な）記号文字列
					sb.append(scanner.next());
				}

				type = TokenUtil.TYPE_SYMBOL;

			} else if (TokenUtil.isNameChar(c)) {
				// キーワード、名称
				do {
					sb.append(c);
					scanner.next();
					c = scanner.peek();
				} while (TokenUtil.isNameChar(c) && c != -1);

				String upper = sb.toString().toUpperCase();

				if (rule.isKeyword(upper)) {
					if (TokenUtil.isSpecialValue(upper)) {
						type = TokenUtil.TYPE_VALUE; // NULL,SYSDATEは値
					} else {
						type = TokenUtil.TYPE_KEYWORD;
					}

				} else {
					type = TokenUtil.TYPE_NAME;
				}

			} else {
				// 不明
				sb.append(scanner.next());
			}

			// トークン生成
			String original = scanner.substring(index);
			token = new Token(original, x, y, index);

			switch (type) {
			case TokenUtil.TYPE_KEYWORD:
				token.setCustom(sb.toString());

				// サブタイプ設定
				if (this.rule.isDataTypes(token.getUpper())) {
					subType = TokenUtil.SUBTYPE_KEYWORD_DATATYPE;

				} else if (this.rule.isFunctions(token.getUpper())) {
					subType = TokenUtil.SUBTYPE_KEYWORD_FUNCTION;
				}
				break;

			case TokenUtil.TYPE_NEW_LINE:
			case TokenUtil.TYPE_EMPTY_LINE:
				token.setCustom(StringUtil.leftTrim(token.getOriginal(),
						TokenUtil.WORD_SEPARATE));
				x = 0;
				break;

			default:
				token.setCustom(sb.toString());
				break;
			}

			token.setType(type);
			token.setSubType(subType);
			token.setDepthParen(depthParen);

			// トークンリストに追加
			this.tokenList.add(token);

			// 次のトークンのための情報
			beforeType = type; // トークン記憶

			// X,Y座標計算
			x += original.length();
			y += incY;

			if (")".equals(sb.toString())) {
				depthParen--;
			}
		}

		// SQL終了トークン
		token = new Token("", x, y, scanner.getLength());
		token.setType(TokenUtil.TYPE_END_SQL);
		this.tokenList.add(token);
	}

	/**
	 * 最適化します。
	 */
	protected void optimize() {

		// 特殊トークン連結
		for (int i = 0; i < tokenList.size(); i++) {
			if(isCanceled()) return;

			Token current = tokenList.getToken(i);
			int next1Index = tokenList.getNextValidTokenIndex(i, 1);
			int next2Index = tokenList.getNextValidTokenIndex(i, 2);
			Token next1 = tokenList.getToken(next1Index);
			Token next2 = tokenList.getToken(next2Index);

			if (current == null || next1 == null)
				continue;

			int currentType = current.getType();
			String currentUpper = current.getUpper();
			int next1Type = next1.getType();
			String next1Upper = next1.getUpper();
			int next2Type = (next2 == null) ? TokenUtil.TYPE_UNKNOWN : next2
					.getType();
			String next2Upper = (next2 == null) ? null : next2.getUpper();

			if (next2 != null
					&& (currentType == TokenUtil.TYPE_KEYWORD
							|| currentType == TokenUtil.TYPE_NAME || currentType == TokenUtil.TYPE_VALUE)
					&& (".".equals(next1Upper))
					&& (currentType == TokenUtil.TYPE_KEYWORD
							|| next2Type == TokenUtil.TYPE_NAME
							|| next2Type == TokenUtil.TYPE_VALUE || "*"
							.equals(next2Upper))) {
				// 『XXXXX . XXXXX』⇒『XXXXX.XXXXX』
				current.setType(TokenUtil.TYPE_NAME);
				current.setOriginal(scanner.getSql().substring(
						current.getIndex(),
						next2.getIndex() + next2.getOriginalLength()));
				current
						.setCustom(current.getCustom() + "."
								+ next2.getCustom());
				tokenList.removeToken(i + 1, next2Index);
				i--;

			} else if (".".equals(currentUpper)
					&& next1.getSubType() == TokenUtil.SUBTYPE_VALUE_NUMERIC) {
				// 『. 99999』⇒『.99999』
				current.setType(TokenUtil.TYPE_VALUE);
				current.setSubType(TokenUtil.SUBTYPE_VALUE_NUMERIC);
				current.setOriginal(scanner.getSql().substring(
						current.getIndex(),
						next1.getIndex() + next1.getOriginalLength()));
				current.setCustom("." + next1.getCustom());
				tokenList.removeToken(i + 1, next1Index);

			} else if (("N".equals(currentUpper) || "Q".equals(currentUpper) || "NQ"
					.equals(currentUpper))
					&& next1.getSubType() == TokenUtil.SUBTYPE_VALUE_STRING) {
				// 『[N,Q] 'XXXXX'』 ⇒ 『[N,Q]'XXXXX'』
				current.setType(TokenUtil.TYPE_VALUE);
				current.setSubType(TokenUtil.SUBTYPE_VALUE_STRING);
				current.setOriginal(scanner.getSql().substring(
						current.getIndex(),
						next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(current.getCustom() + next1.getCustom());
				tokenList.removeToken(i + 1, next1Index);
				i -= 2;

			} else if ((currentType == TokenUtil.TYPE_KEYWORD
					|| currentType == TokenUtil.TYPE_NAME || currentType == TokenUtil.TYPE_VALUE)
					&& (".".equals(next1Upper))) {
				// 『XXXXX .』 ⇒ 『XXXXX.』
				current.setType(TokenUtil.TYPE_NAME);
				current.setOriginal(scanner.getSql().substring(
						current.getIndex(),
						next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(current.getCustom() + ".");
				tokenList.removeToken(i + 1, next1Index);

			} else if (current.getSubType() == TokenUtil.SUBTYPE_VALUE_BIND
					&& ":".equals(currentUpper)
					&& next1Type == TokenUtil.TYPE_NAME) {
				// 『: XXXXX』⇒『:XXXXX』
				current.setOriginal(scanner.getSql().substring(
						current.getIndex(),
						next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(":" + next1.getCustom());
				tokenList.removeToken(i + 1, next1Index);
// <-- modify zigen
			} else if (current.getSubType() == TokenUtil.SUBTYPE_VALUE_BIND
					&& ":".equals(currentUpper)
					&& next1Type == TokenUtil.TYPE_VALUE) {
				// 『: 99999』⇒『:99999』
				current.setOriginal(scanner.getSql().substring(
						current.getIndex(),
						next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(":" + next1.getCustom());
				tokenList.removeToken(i + 1, next1Index);
// -->
			} else {
				// SQL区切り文字設定
				setSqlSeparator(current, i);
			}

		}
		// 複合キーワードトークン連結
		for (int i = 0; i < tokenList.size() - 1; i++) {
			if(isCanceled()) return;

			Token current = tokenList.getToken(i);
			int next1Index = tokenList.getNextValidTokenIndex(i, 1);
			Token next1 = tokenList.getToken(next1Index);

			if (current.getType() != TokenUtil.TYPE_KEYWORD)
				continue;

			if (next1 == null || next1.getType() != TokenUtil.TYPE_KEYWORD)
				continue;

			int next2Index = tokenList.getNextValidTokenIndex(i, 2);
			int next3Index = tokenList.getNextValidTokenIndex(i, 3);
			Token next2 = tokenList.getToken(next2Index);
			Token next3 = tokenList.getToken(next3Index);

			if (next3 != null && next3.getType() == TokenUtil.TYPE_KEYWORD
					&& next2 != null
					&& next2.getType() == TokenUtil.TYPE_KEYWORD) {
				// ４キーワード
				StringBuffer sb4 = new StringBuffer();
				sb4.append(current.getCustom()).append(' ');
				sb4.append(next1.getCustom()).append(' ');
				sb4.append(next2.getCustom()).append(' ');
				sb4.append(next3.getCustom());

				if (TokenUtil.isMultiKeyword(sb4.toString().toUpperCase())) {
					current.setOriginal(scanner.getSql().substring(
							current.getIndex(),
							next3.getIndex() + next3.getOriginalLength()));
					current.setCustom(sb4.toString());
					tokenList.set(i, current);
					tokenList.removeToken(i + 1, next3Index);
					continue;
				}
			}

			if (next2 != null && next2.getType() == TokenUtil.TYPE_KEYWORD) {
				// ３キーワード
				StringBuffer sb3 = new StringBuffer();
				sb3.append(current.getCustom()).append(' ');
				sb3.append(next1.getCustom()).append(' ');
				sb3.append(next2.getCustom());

				if (TokenUtil.isMultiKeyword(sb3.toString().toUpperCase())) {
					current.setOriginal(scanner.getSql().substring(
							current.getIndex(),
							next2.getIndex() + next2.getOriginalLength()));
					current.setCustom(sb3.toString());
					tokenList.set(i, current);
					tokenList.removeToken(i + 1, next2Index);
					continue;
				}
			}

			// ２キーワード
			StringBuffer sb2 = new StringBuffer();
			sb2.append(current.getCustom()).append(' ');
			sb2.append(next1.getCustom());

			if (TokenUtil.isMultiKeyword(sb2.toString().toUpperCase())) {
				current.setOriginal(scanner.getSql().substring(
						current.getIndex(),
						next1.getIndex() + next1.getOriginalLength()));
				current.setCustom(sb2.toString());
				tokenList.set(i, current);
				tokenList.removeToken(i + 1, next1Index);
			}
		}

		// トークン詳細情報設定
		int size = tokenList.size();
		for (int i = 0; i < size; i++) {
			if(isCanceled()) return;

			Token token = this.tokenList.getToken(i);
			if (token.getType() != TokenUtil.TYPE_SYMBOL)
				continue;

			if ("(".equals(token.getUpper())) {
				// 括弧内要素設定
				setInParenInfo(i);
			}
		}
	}

	/**
	 * 『[/,;]』（SQL区切り文字）設定
	 *
	 * @param token
	 * @param i
	 */
	private void setSqlSeparator(Token token, int index) {
		String upper = token.getUpper();
		if (upper.length() != 1 || !TokenUtil.isSqlSeparate(upper.charAt(0)))
			return;

		int len = tokenList.size();
		for (int i = index; i < len; i++) {
			if(isCanceled()) return;

			Token current = this.tokenList.getToken(i);
			int type = current.getType();
			upper = current.getUpper();

			switch (type) {
			case TokenUtil.TYPE_NAME:
			case TokenUtil.TYPE_VALUE:
			case TokenUtil.TYPE_SQL_SEPARATE:
				return;

			case TokenUtil.TYPE_SYMBOL:
				if ("(".equals(upper))
					continue;

				if (TokenUtil.isSqlSeparate(upper.charAt(0))) // ';'
					break;
				return;

			case TokenUtil.TYPE_OPERATOR:
				if (TokenUtil.isSqlSeparate(upper.charAt(0))) // '/'
					continue;
				return;

			case TokenUtil.TYPE_KEYWORD:
				if (TokenUtil.isBeginSqlKeyword(upper))
					break;
				return;

			case TokenUtil.TYPE_END_SQL:
				break;

			default:
				continue;
			}
			break;
		}

		// SQL区切り文字設定
		token.setType(TokenUtil.TYPE_SQL_SEPARATE);

		Token beforeToken = this.tokenList.getToken(index - 1);
		if (beforeToken != null) {
			int beforeType = beforeToken.getType();

			switch (beforeType) {
			case TokenUtil.TYPE_NEW_LINE:
				token.setOriginal(beforeToken.getOriginal()
						+ token.getOriginal());
				token
						.setCustom(rule.getOutNewLineCodeStr()
								+ token.getCustom());
				token.setIndex(beforeToken.getIndex());
				token.setX(beforeToken.getX());
				token.setY(beforeToken.getY());
				this.tokenList.remove(index - 1);
				index--;
				break;

			case TokenUtil.TYPE_BEGIN_SQL:
			case TokenUtil.TYPE_SQL_SEPARATE:
				token.setCustom(token.getCustom());
				break;

			default:
				// token.setOriginal(beforeToken.getOriginal()
				// + token.getOriginal());
				token
						.setCustom(rule.getOutNewLineCodeStr()
								+ token.getCustom());
				// token.setIndex(beforeToken.getIndex());
				// token.setX(beforeToken.getX());
				// token.setY(beforeToken.getY());
				break;
			}
		}

		Token nextToken = this.tokenList.getToken(index + 1);
		if (nextToken != null) {
			int nextType = nextToken.getType();
			switch (nextType) {
			case TokenUtil.TYPE_NEW_LINE:
				token
						.setOriginal(token.getOriginal()
								+ nextToken.getOriginal());
				token
						.setCustom(token.getCustom()
								+ rule.getOutNewLineCodeStr());
				this.tokenList.remove(index + 1);

			case TokenUtil.TYPE_END_SQL:
				break;

			default:
				token.setOriginal(token.getOriginal());
				token
						.setCustom(token.getCustom()
								+ rule.getOutNewLineCodeStr());
				break;
			}
		}
	}

	/**
	 * 括弧内情報（要素数、要素番号、値のみフラグ、親要素）を設定します。
	 *
	 * @param startPos
	 */
	private void setInParenInfo(int startPos) {
		int deep = 1;
		int elementLength = 0; // 括弧内の容素数
		boolean valueOnly = true; // 値のみであるか
		int size = tokenList.size();
		Token parentTokenInParen = tokenList.getParentTokenInParen(startPos);

		Token nextToken = this.tokenList.getToken(startPos - 1);
		if (nextToken != null && !")".equals(nextToken.getUpper())) {
			elementLength++;
		}

		for (int i = startPos + 1; i < size; i++) {
			if(isCanceled()) return;

			Token current = this.tokenList.getToken(i);
			int type = current.getType();
			String upper = current.getUpper();

			if ("(".equals(upper)) {
				deep++;
				current.setElementIndexInParen(0);

			} else if (")".equals(upper)) {
				deep--;
				current.setElementIndexInParen(0);

				if (deep == 0) {
					// 『(』～『)』まで括弧内要素数、値のみフラグ、親要素を設定
					for (int j = startPos; j <= i; j++) {
						current = this.tokenList.getToken(j);
						current.setElementLengthInParen(elementLength);
						current.setValueOnlyInParen(valueOnly);
						current.setParentTokenInParen(parentTokenInParen);
					}
					return;
				}

			} else {
				current.setElementIndexInParen(elementLength);

				switch (type) {
				case TokenUtil.TYPE_KEYWORD:
					valueOnly = false;
					break;

				case TokenUtil.TYPE_NAME:
				case TokenUtil.TYPE_VALUE:
				case TokenUtil.TYPE_OPERATOR:
					break;

				case TokenUtil.TYPE_SYMBOL:
					if (deep == 1 && ",".equals(upper)) {
						current.setElementIndexInParen(elementLength);
						elementLength++;

					} else if ("(".equals(upper)) {
						valueOnly = false;
					}
					break;

				default:
					// valueOnly = false;
					break;
				}
			}
		}
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		// TODO 自動生成されたメソッド・スタブ
	}
}
