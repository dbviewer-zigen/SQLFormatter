/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.tokenizer;

public class Token {
	// オリジナル文字列
	private String original;

	// 大文字文字列（内部判定用）
	private String upper;

	// カスタム文字列
	private String custom;

	// タイプ
	private int type;

	// サブタイプ
	private int subType;

	// 列
	private int x;

	// 行
	private int y;

	// 先頭からの文字数
	private int index;

	// 括弧内の要素数
	private int elementLengthInParen;

	// 括弧内の要素番号
	private int elementIndexInParen;

	// 括弧内 値のみか
	private boolean valueOnlyInParen;

	// 括弧内 親要素
	private Token parentTokenInParen;

	// 括弧の深さ
	private int depthParen;

	// インデント数
	private int indent;

	/**
	 * コンストラクタ
	 * 
	 * @param original
	 * @param x
	 * @param y
	 * @param index
	 */
	public Token(String original, int x, int y, int index) {
		this.original = original;
		this.upper = "";
		this.custom = "";
		this.x = x;
		this.y = y;
		this.index = index;
		this.elementLengthInParen = 0;
		this.elementIndexInParen = 0;
		this.valueOnlyInParen = false;
		this.parentTokenInParen = null;
		this.depthParen = 0;
		this.indent = 0;
	}

	/**
	 * コピーコンストラクタ
	 * 
	 * @param token
	 */
	public Token(Token token) {
		this.original = token.getOriginal();
		this.upper = token.getUpper();
		this.custom = token.getCustom();
		this.x = token.getX();
		this.y = token.getY();
		this.index = token.getIndex();
		this.elementLengthInParen = token.getElementLengthInParen();
		this.elementIndexInParen = token.getElementIndexInParen();
		this.valueOnlyInParen = token.isValueOnlyInParen();
		this.parentTokenInParen = token.getParentTokenInParen();
		this.depthParen = token.getDepthParen();
		this.indent = token.getIndent();
	}

	/**
	 * @return the original
	 */
	public String getOriginal() {
		return original;
	}

	/**
	 * @param original
	 *            the original to set
	 */
	public void setOriginal(String original) {
		this.original = original;
	}

	/**
	 * @return the upper
	 */
	public String getUpper() {
		return upper;
	}

	/**
	 * @param upper
	 *            the upper to set
	 */
	public void setUpper(String upper) {
		this.upper = upper.toUpperCase();
	}

	/**
	 * @return the custom
	 */
	public String getCustom() {
		return custom;
	}

	/**
	 * @param custom
	 *            the custom to set
	 */
	public void setCustom(String custom) {
		this.custom = custom;
		setUpper(custom);
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the subType
	 */
	public int getSubType() {
		return subType;
	}

	/**
	 * @param subType
	 *            the subType to set
	 */
	public void setSubType(int subType) {
		this.subType = subType;
	}

	/**
	 * @return the elementLengthInParen
	 */
	public int getElementLengthInParen() {
		return elementLengthInParen;
	}

	/**
	 * @param elementLengthInParen
	 *            the elementLengthInParen to set
	 */
	public void setElementLengthInParen(int elementLengthInParen) {
		this.elementLengthInParen = elementLengthInParen;
	}

	/**
	 * @return the valueOnlyInParen
	 */
	public boolean isValueOnlyInParen() {
		return valueOnlyInParen;
	}

	/**
	 * @param valueOnlyInParen
	 *            the valueOnlyInParen to set
	 */
	public void setValueOnlyInParen(boolean valueOnlyInParen) {
		this.valueOnlyInParen = valueOnlyInParen;
	}

	public int getOriginalLength() {
		return original.length();
	}

	public int getCustomLength() {
		return custom.length();
	}

	/**
	 * @return the parentTokenInParen
	 */
	public Token getParentTokenInParen() {
		return parentTokenInParen;
	}

	/**
	 * @param parentTokenInParen
	 *            the parentTokenInParen to set
	 */
	public void setParentTokenInParen(Token parentTokenInParen) {
		this.parentTokenInParen = parentTokenInParen;
	}

	/**
	 * @return the ElementIndexInParen
	 */
	public int getElementIndexInParen() {
		return elementIndexInParen;
	}

	/**
	 * @param ElementIndexInParen
	 *            the ElementIndexInParen to set
	 */
	public void setElementIndexInParen(int elementIndexInParen) {
		this.elementIndexInParen = elementIndexInParen;
	}

	/**
	 * @return the depthParen
	 */
	public int getDepthParen() {
		return depthParen;
	}

	/**
	 * @param depthParen
	 *            the depthParen to set
	 */
	public void setDepthParen(int depthParen) {
		this.depthParen = depthParen;
	}

	/**
	 * @return the indent
	 */
	public int getIndent() {
		return indent;
	}

	/**
	 * @param indent
	 *            the indent to set
	 */
	public void setIndent(int indent) {
		this.indent = indent;
	}

	/**
	 * @generated by CodeSugar http://sourceforge.net/projects/codesugar
	 */

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Token:");
		buffer.append(" original: ");
		buffer.append(TokenUtil.debugString(original));
		buffer.append(" upper: ");
		buffer.append(TokenUtil.debugString(upper));
		buffer.append(" custom: ");
		buffer.append(TokenUtil.debugString(custom));
		buffer.append(" type: ");
		buffer.append(TokenUtil.debugTypeString(type));
		buffer.append(" subType: ");
		buffer.append(TokenUtil.debugSubTypeString(subType));
		buffer.append(" x: ");
		buffer.append(x);
		buffer.append(" y: ");
		buffer.append(y);
		buffer.append(" index: ");
		buffer.append(index);
		buffer.append(" elementLengthInParen: ");
		buffer.append(elementLengthInParen);
		buffer.append(" elementIndexInParen: ");
		buffer.append(elementIndexInParen);
		buffer.append(" valueOnlyInParen: ");
		buffer.append(valueOnlyInParen);
		buffer.append(" parentTokenInParen: ");
		buffer.append("『"
				+ ((parentTokenInParen != null) ? parentTokenInParen
						.getCustom() : "null") + "』");
		buffer.append(" depthParen: ");
		buffer.append(depthParen);
		buffer.append(" indent: ");
		buffer.append(indent);
		buffer.append("]");
		return buffer.toString();
	}
}
