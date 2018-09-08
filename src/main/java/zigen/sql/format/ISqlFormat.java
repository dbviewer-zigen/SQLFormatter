package zigen.sql.format;

public interface ISqlFormat {

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormat#format(java.lang.String)
	 */
	public abstract String format(String sql) throws SqlFormatException;

	/**
	 * フォーマット処理（インデントオフセット有）
	 * 
	 * @param sql
	 * @param offset
	 * @return
	 * @throws SqlFormatException
	 */
	public abstract String format(String sql, int offset)
			throws SqlFormatException;

	/*
	 * (非 Javadoc)
	 * 
	 * @see kry.sql.format.ISqlFormat#unFormat(java.lang.String)
	 */
	public abstract String unFormat(String sql);

}