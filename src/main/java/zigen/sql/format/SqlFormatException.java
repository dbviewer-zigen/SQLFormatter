/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.format;

import java.io.IOException;

public class SqlFormatException extends IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4520711020063464106L;

	public SqlFormatException() {
		super();
	}

	/**
	 * コンストラクタ
	 * 
	 * @param str
	 * @param token
	 */
	public SqlFormatException(String arg) {
		super(arg);
	}
}
