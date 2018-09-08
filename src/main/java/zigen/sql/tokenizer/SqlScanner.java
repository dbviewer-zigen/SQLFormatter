/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.tokenizer;

public class SqlScanner {
	// 読取りSQL
	private String sql;

	private int length;

	private int current;

	/**
	 * コンストラクタ
	 *
	 * @param sql
	 */
	public SqlScanner(String sql) {
		this.sql = sql;
		this.length = (sql == null) ? 0 : sql.length();
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the current
	 */
	public int getCurrent() {
		return current;
	}

	public boolean hasNext() {
		return (sql != null) && (current < length);
	}

	/**
	 * 現在の位置から指定されたインデックス目の文字があるか返します。
	 *
	 * @return
	 */
	public boolean hasNext(int i) {
		return (sql != null) && (current + i < length);
	}

	public char next() {
		if (!hasNext()) {
			return (char) -1;
		}
		return sql.charAt(current++);
	}

	public char peek() {
		if (!hasNext()) {
			return (char) -1;
		}
		return sql.charAt(current);
	}

	/**
	 * 現在の位置から指定されたインデックス目の文字を取得します。
	 *
	 * @return
	 */
	public char peek(int i) {
		if (!hasNext(i)) {
			return (char) -1;
		}
		return sql.charAt(current + i);
	}

	/**
	 * スペース・タブをスキップします。
	 *
	 * @return
	 */
	public int skipSpaceTab() {
		int count = 0;
		char c = peek();
		while (c == ' ' || c == '\t') {
			next();
			c = peek();
			count++;
		}

		return count;
	}

	/**
	 * 現在の位置から指定された文字列と等しいか返します。
	 *
	 * @return
	 */
	public boolean isPeekEquals(String str) {
		if (this.sql == null && str == null)
			return true;

		if (this.length > 0 && str == null)
			return false;

		int len = str.length();
		if (this.length - current < len)
			return false;

		for (int i = 0; i < len; i++) {
			if (peek(i) != str.charAt(i))
				return false;
		}
		return true;
	}

	/**
	 * 現在の位置から指定された文字配列と等しいか返します。
	 *
	 * @return
	 */
	public boolean isPeekEquals(String[] strs) {
		if (strs == null || strs.length == 0)
			return false;

		for (int i = strs.length - 1; i >= 0; i--) {
			if (isPeekEquals(strs[i]))
				return true;
		}
		return false;
	}

	/**
	 * 現在の位置から指定された文字列と等しいか返します。 (タブ・スペース・改行文字呼み飛ばし)
	 *
	 * @return
	 */
	public boolean isPeekEqualsEx(String str) {
		if (this.sql == null && str == null)
			return true;

		if (this.length > 0 && str == null)
			return false;

		int len = str.length();
		if (this.length - current < len)
			return false;

		int pos = 0;
		for (int i = 0; i < len; i++) {
			pos = skipTabSpaceNewLine(pos);
			if (peek(pos) != str.charAt(i))
				return false;
			pos++;
		}
		return true;
	}

	/**
	 * 現在の位置から指定された文字列と等しいか返します。(タブ・スペース・改行文字呼み飛ばし) 等しい場合、自動next()
	 *
	 * @param str
	 * @return
	 */
	public boolean isPeekNextEqualsEx(String str) {
		if (this.sql == null && str == null)
			return true;

		if (this.length > 0 && str == null)
			return false;

		int len = str.length();
		if (this.length - current < len)
			return false;

		int pos = 0;
		for (int i = 0; i < len; i++) {
			pos = skipTabSpaceNewLine(pos);
			if (peek(pos) != str.charAt(i))
				return false;
			pos++;
		}

		current += pos;
		return true;
	}

	/**
	 * 現在の位置から指定された文字列と等しいか返します。(タブ・スペース・改行文字呼み飛ばし) 等しい場合、自動next()
	 *
	 * @param strs
	 * @return
	 */
	public String getPeekNextEqualsExString(String[] strs) {
		if (this.sql == null && strs == null)
			return null;

		if (this.length > 0 && strs == null)
			return null;

		for (int i = strs.length - 1; i >= 0; i--) {
			String str = strs[i];
			int len = str.length();
			if (this.length - current < len)
				continue;

			boolean isFind = true;
			int pos = 0;
			for (int j = 0; j < len; j++) {
				pos = skipTabSpaceNewLine(pos);
				if (peek(pos) != str.charAt(j)) {
					isFind = false;
					break;
				}
				pos++;
			}

			if (!isFind)
				continue;

			current += pos;
			return strs[i];
		}

		return null;
	}

	/**
	 * 指定されたインデックスから、タブ・スペース・改行文字を飛ばします。
	 *
	 * @param start
	 * @return
	 */
	private int skipTabSpaceNewLine(int start) {
		int pos = start;
		char c = peek(pos);
		while (TokenUtil.isWordSeparate(c) || TokenUtil.isNewLineChar(c))
			c = peek(++pos);

		return pos;
	}

	/**
	 * 指定した位置から現在位置までの部分文字列を返します。
	 *
	 * @param beginIndex
	 * @return
	 */
	public String substring(int beginIndex) {
		int len = current - beginIndex;
		if (len < 0)
			return null;
		return this.sql.substring(beginIndex, current);
	}
}
