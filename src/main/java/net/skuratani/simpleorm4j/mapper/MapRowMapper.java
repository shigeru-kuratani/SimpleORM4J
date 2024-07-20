package net.skuratani.simpleorm4j.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import net.skuratani.simpleorm4j.exception.So4jException;

/**
 * <p>マップROWマッパ</p>
 * <pre>
 * 検索データのカラム名をキーとしてMap(HashMap)にROWデータをマッピングする。
 * (ex.) 検索カラムがSCHOOL.IDの場合、生成されるMapインスタンスの
 *       IDキーに検索値がマッピングされる。
 * </pre>
 */
public class MapRowMapper<T> implements IfRowMapper<T> {

	/** マッピング対象クラス */
	protected Class<T> _clazz;

	/**
	 * <p>コンストラクタ</p>
	 *
	 * @param clazz マッピングエンティティクラス
	 */
	public MapRowMapper(Class<T> clazz) {
		_clazz = clazz;
	}

	/**
	 * <p>ROWマップ処理</p>
	 *
	 * @param  resultSet リザルトセット
	 * @return マッピングインスタンス
	 * @throws So4jException リザルトセットからメタデータの取得に失敗した場合<br>
	 *                       リザルトセットからデータの取得に失敗した場合
	 */
	@SuppressWarnings("unchecked")
	public T mapRow(ResultSet resultSet) throws So4jException {

		try {
			// メタデータ
			ResultSetMetaData rsmd = resultSet.getMetaData();

			// ユーザインスタンス
			Map<String, Object> rowMap = new HashMap<>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String key = rsmd.getColumnLabel(i);
				// String型
				if (resultSet.getObject(i) instanceof String) {
					rowMap.put(key, String.valueOf(resultSet.getString(i)));
				// int型
				} else if (resultSet.getObject(i) instanceof Integer) {
					rowMap.put(key, Integer.valueOf(resultSet.getInt(i)));
				// long型
				} else if (resultSet.getObject(i) instanceof Long) {
					rowMap.put(key, Long.valueOf(resultSet.getInt(i)));
				// float型
				} else if (resultSet.getObject(i) instanceof Float) {
					rowMap.put(key, Float.valueOf(resultSet.getInt(i)));
				// double型
				} else if (resultSet.getObject(i) instanceof Double) {
					rowMap.put(key, Double.valueOf(resultSet.getInt(i)));
				// その他
				} else {
					rowMap.put(key, resultSet.getObject(i));
				}
			}
			return (T) rowMap;
		} catch (Exception e) {
			throw new So4jException(e.getMessage(), e);
		}
	}
}
