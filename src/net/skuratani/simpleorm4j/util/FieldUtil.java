package net.skuratani.simpleorm4j.util;

import java.lang.reflect.Field;

import net.skuratani.simpleorm4j.annotation.Column;
import net.skuratani.simpleorm4j.annotation.Table;

/**
 * <p>フィールドに関するユーティリティクラス</p>
 * <pre>
 * エンティティクラスのフィールドに関するユーティリティクラス。
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.1
 */
public class FieldUtil {

	/**
	 * <p>フィールドのカラム名を取得</p>
	 * <pre>
	 * エンティティクラスのフィールドから「テーブル名.カラム名」を取得する。
	 * </pre>
	 *
	 * @param  field エンティティフィールド
	 * @return カラム名
	 */
	public static String getFiledName(Field field) {
		StringBuilder builder = new StringBuilder();
		Class<?> clazz = field.getDeclaringClass();
		if (AnnotationUtil.hasEntityAnnotation(clazz)) {
			builder.append(AnnotationUtil.hasTableAnnotation(clazz)
							? clazz.getDeclaredAnnotation(Table.class).name()
							: clazz.getSimpleName());
			builder.append(".");
			builder.append(AnnotationUtil.hasColumnAnnotation(field)
							? field.getDeclaredAnnotation(Column.class).name()
							: field.getName());
		}
		return builder.toString();
	}
}
