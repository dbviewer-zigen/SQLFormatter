/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

	// 改行コード
	public static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * 指定されたパス配下のファイル名一覧を返します。
	 * 
	 * @param path
	 * @return
	 */
	public static String[] getChild(String path) {
		File file = new File(path);
		return file.list();
	}

	/**
	 * ファイルの内容を文字列で返します。
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getFileString(String path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));

		StringBuffer sb = new StringBuffer();

		String str = null;
		while ((str = reader.readLine()) != null) {
			sb.append(str);
			sb.append(NEW_LINE);
		}

		int len = sb.length();
		if (len > 0) {
			sb.delete(len - NEW_LINE.length(), len);
		}

		return sb.toString();
	}

}
