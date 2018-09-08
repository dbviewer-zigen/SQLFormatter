/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.tokenizer;

import java.util.LinkedList;

public class TokenList extends LinkedList {

	private static final long serialVersionUID = -7668336470690425446L;

	public Token getToken(int index) {
		if (index < 0 || size() - 1 < index)
			return null;
		return (Token) super.get(index);
	}

	public Token getFirstToken() {
		return (Token) super.getFirst();
	}

	public Token getLastToken() {
		return (Token) super.getLast();
	}

	/**
	 * 指定されたstartからindex番目の有効トークンインデックスを取得します。
	 *
	 * @param start
	 * @param index
	 * @return
	 */
	public int getNextValidTokenIndex(int start, int index) {
		start++;
		if (start < 0 || this.size() - 1 < start || index <= 0)
			return -1;

		int count = 0;
		int len = this.size();
		for (int i = start; i < len; i++) {
			Token token = this.getToken(i);
			switch (token.getType()) {
			case TokenUtil.TYPE_NEW_LINE:
				continue;

			default:
				count++;
				if (index <= count)
					return i;
				continue;
			}
		}
		return -1;
	}

	/**
	 * 指定された開始インデックスから終了インデックスまでのトークンを削除します。
	 *
	 * @param start
	 * @param end
	 */
	public void removeToken(int start, int end) {
		if (start < 0 || this.size() - 1 < start || end < 0
				|| this.size() - 1 < end)
			return;

		if (start > end)
			return;

		for (int i = end; i >= start; i--) {
			this.remove(i);
		}
	}

	/**
	 * 指定されたインデックスの括弧の親要素を取得します。
	 *
	 * @param index
	 * @return
	 */
	public Token getParentTokenInParen(int index) {
		if (index - 1 <= 0)
			return null;

		for (int i = index - 1; i >= 0; i--) {
			Token token = (Token) super.get(i);

			switch (token.getType()) {
			case TokenUtil.TYPE_KEYWORD:
				// int subType = token.getSubType();
				// String upper = token.getCustom().toUpperCase();

				// if (subType == TokenUtil.SUBTYPE_KEYWORD_DATATYPE
				// || subType == TokenUtil.SUBTYPE_KEYWORD_FUNCTION
				// || "IN".equals(upper))
				return token;
				// return null;

			case TokenUtil.TYPE_NAME:
				return token;

			case TokenUtil.TYPE_COMMENT:
			case TokenUtil.TYPE_EMPTY_LINE:
			case TokenUtil.TYPE_NEW_LINE:
				continue;

			default:
				return null;
			}
		}

		return null;
	}

	/**
	 * 指定されたインデックスのトークンを連結します。
	 *
	 * @param start
	 * @param end
	 * @param originalSql
	 * @param joinStr
	 */
	// public boolean joinToken(int start, int end, String originalSql) {
	// return joinToken(start, end, originalSql, "");
	// }
	//
	// public boolean joinToken(int start, int end, String originalSql,
	// String joinStr) {
	// if (start >= end)
	// return false;
	// if (start < 0 || start > this.size() - 1)
	// return false;
	// if (end < 0 || end > this.size() - 1)
	// return false;
	//
	// joinStr = (joinStr == null) ? "" : joinStr;
	// Token startToken = getToken(start);
	// Token endToken = getToken(end);
	// if (startToken == null || endToken == null)
	// return false;
	//
	// StringBuffer sb = new StringBuffer();
	// for (int i = start; i <= end; i++) {
	// Token current = (Token) this.getToken(i);
	//
	// if (i != start)
	// sb.append(joinStr);
	// sb.append(current.getCustom());
	// }
	// // オリジナルを設定
	// startToken.setOriginal(originalSql.substring(startToken.getIndex(),
	// endToken.getIndex() + endToken.getOriginalLength()));
	// // カスタムを設定
	// startToken.setCustom(sb.toString());
	//
	// for (int i = end; i > start; i--)
	// this.remove(i);
	//
	// return true;
	// }
	/**
	 * @generated by CodeSugar http://sourceforge.net/projects/codesugar
	 */

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[TokenList:");/*
										 * Inaccessible getter for private field
										 * header
										 */
		/* Inaccessible getter for private field size */
		buffer.append(" modCount: ");
		buffer.append(modCount);
		buffer.append("]");
		return buffer.toString();
	}
}
