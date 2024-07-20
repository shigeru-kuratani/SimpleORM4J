package net.skuratani.simpleorm4j.mapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.skuratani.simpleorm4j.annotation.Column;
import net.skuratani.simpleorm4j.exception.So4jException;

/**
 * <p>エンティティプロパティROWマッパ</p>
 * <pre>
 * エンティティクラスのフィールドに、検索ROWデータと同じ検索カラム or 同じColumnアノテーションの値をマッピングする。
 * (ex.) 1. Columnアノテーションを指定しない場合
 *          検索カラム：SCHOOL.ID
 *          フィールド：SCHOOLクラスのIDフィールド
 *          → 検索ROWデータのID値が、SCHOOLクラスのIDフィールドに代入される。
 *       2. Columnアノテーションを指定する場合
 *          検索カラム：SCHOOL.ID
 *          フィールド：SCHOOLクラスのschoolIdフィールドに「＠Column(name="ID")」を指定
 *          → 検索ROWデータのID値が、SCHOOLクラスのschoolIdフィールドに代入される。
 * </pre>
 */
public class EntityPropertyRowMapper<T> implements IfRowMapper<T> {

	/** マッピング対象クラス */
	protected Class<T> _clazz;

	/**
	 * <p>コンストラクタ</p>
	 *
	 * @param clazz マッピングエンティティクラス
	 */
	public EntityPropertyRowMapper(Class<T> clazz) {
		_clazz = clazz;
	}

	/**
	 * <p>ROWマップ処理</p>
	 *
	 * @param  resultSet リザルトセット
	 * @return マッピングインスタンス
	 * @throws So4jException クラスインスタンスの生成の失敗した場合<br>
	 * 　　　　　　　　　　    リザルトセットからメタデータの取得に失敗した場合<br>
	 *                       エンティティフィールドのセッタメソッドのアクセスに失敗した場合<br>
	 *                       エンティティフィールドのセッタメソッドの実行に失敗した場合
	 */
	public T mapRow(ResultSet resultSet) throws So4jException {

		try {
			// フィールドリスト
			Field[] fields = this.getAllFields();
			// メタデータ
			ResultSetMetaData rsmd = resultSet.getMetaData();

			// ユーザインスタンス
			T instance = _clazz.getConstructor().newInstance();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				for (Field field : fields) {
					if (   rsmd.getColumnName(i).equals(field.getName())
						|| rsmd.getColumnName(i).equals(
								field.getAnnotation(Column.class) != null
									? field.getAnnotation(Column.class).name() : "")) {

						PropertyDescriptor prop = new PropertyDescriptor(field.getName(), instance.getClass());
						Method setter = prop.getWriteMethod();
						// String型
						if (field.getType().getSimpleName().equals("String")) {
							setter.invoke(instance, String.valueOf(resultSet.getString(i)));
						// int型
						} else if (   field.getType().getSimpleName().equals("int")
								   || field.getType().getSimpleName().equals("Integer")) {
							setter.invoke(instance, Integer.valueOf(resultSet.getInt(i)));
						// long型
						} else if (   field.getType().getSimpleName().equals("long")
								   || field.getType().getSimpleName().equals("Long")) {
							setter.invoke(instance, Long.valueOf(resultSet.getInt(i)));
						// float型
						} else if (   field.getType().getSimpleName().equals("float")
								   || field.getType().getSimpleName().equals("Float")) {
							setter.invoke(instance, Float.valueOf(resultSet.getInt(i)));
						// double型
						} else if (   field.getType().getSimpleName().equals("double")
								   || field.getType().getSimpleName().equals("Double")) {
							setter.invoke(instance, Double.valueOf(resultSet.getInt(i)));
						// LocalDate型
						} else if (field.getType().getSimpleName().equals("LocalDate")) {
							setter.invoke(instance, resultSet.getDate(i).toLocalDate());
						// LocalDateTime型
						} else if (field.getType().getSimpleName().equals("LocalDateTime")) {
							setter.invoke(instance, resultSet.getTimestamp(i).toLocalDateTime());
						// その他
						} else {
							setter.invoke(instance, resultSet.getObject(i));
						}
					}
				}
			}
			return instance;
		} catch (Exception e) {
			throw new So4jException(e.getMessage(), e);
		}
	}

	/**
	 * <p>全フィールドの配列を取得</p>
	 * <pre>
	 * コンストラクタ引数のクラスに定義されている全フィールド配列を取得する。
	 * ※スーパークラスも含めて全て取得する。
	 * </pre>
	 *
	 * @return フィールド配列
	 */
	protected Field[] getAllFields() {
		List<Field> fieldList = new ArrayList<Field>();
		for (Class<?> clazz = _clazz; clazz != null; clazz = clazz.getSuperclass()) {
			fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
		}
		return fieldList.toArray(new Field[0]);
	}
}
