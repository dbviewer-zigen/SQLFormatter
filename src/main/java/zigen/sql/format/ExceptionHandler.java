/*
 * Copyright (c) 2007 - 2009 ZIGEN
 * Eclipse Public License - v 1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.format;

import zigen.sql.tokenizer.Token;

public class ExceptionHandler {
	public static void handleException(String message, Token token)
			throws SqlFormatException {
		StringBuffer sb = new StringBuffer();
		sb.append(message);
		sb.append(":\n");
		sb.append(token.toString());
		throw new SqlFormatException(sb.toString());
	}
}
