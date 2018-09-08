/*
 * 作成日: 2007/11/30
 * 著作権: Copyright (c) 2007 kry
 * ライセンス：Eclipse Public License - v 1.0
 * 原文：http://www.eclipse.org/legal/epl-v10.html
 */
package zigen.sql.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ArrayUtil {

	/**
	 * 指定された配列を追加します。 ※ null、同じ項目は追加しない。
	 * 
	 * @param array1
	 * @param array2
	 * @return ソート済み配列
	 */
	public static Object[] add(Object[] array1, Object obj2) {
		Object[] array2 = { obj2 };
		return add(array1, array2, null);
	}

	public static Object[] add(Object[] array1, Object[] array2) {
		return add(array1, array2, null);
	}

	/**
	 * 指定された配列を追加します。 ※ null、同じ項目は追加しない。（配列変換有）
	 * 
	 * @param array1
	 * @param array2
	 * @param cast
	 * @return
	 */
	public static Object[] add(Object[] array1, Object obj2, Object[] conv) {
		Object[] array2 = { obj2 };
		return add(array1, array2, conv);
	}

	public static Object[] add(Object[] array1, Object[] array2, Object[] conv) {
		if (array1 != null && array2 == null)
			return array1;
		if (array1 == null && array2 != null)
			return array2;

		List list = new LinkedList(Arrays.asList(array1));

		for (int i = 0; i < array1.length; i++) {
			if (array1[i] != null)
				list.add(array1[i]);
		}

		for (int i = 0; i < array2.length; i++) {
			if (!list.contains(array2[i]) && array2[i] != null)
				list.add(array2[i]);
		}

		if (conv == null)
			return list.toArray();
		return list.toArray(conv);
	}

	/**
	 * 指定されたarray1からarray2の項目を削除します。
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static Object[] subtract(Object[] array1, Object obj2) {
		Object[] array2 = { obj2 };
		return subtract(array1, array2, null);
	}

	public static Object[] subtract(Object[] array1, Object[] array2) {
		return subtract(array1, array2, null);
	}

	/**
	 * 指定されたarray1からarray2の項目を削除します。（配列変換有）
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static Object[] subtract(Object[] array1, Object obj2, Object[] conv) {
		Object[] array2 = { obj2 };
		return subtract(array1, array2, conv);
	}

	public static Object[] subtract(Object[] array1, Object[] array2,
			Object[] conv) {
		if (array1 == null || array1.length == 0 || array2 == null
				|| array2.length == 0)
			return array1;

		LinkedList tree = new LinkedList(Arrays.asList(array1));

		for (int i = 0; i < array2.length; i++) {
			if (tree.contains(array2[i]))
				tree.remove(array2[i]);
		}

		if (conv == null)
			return tree.toArray();
		return tree.toArray(conv);
	}
}
