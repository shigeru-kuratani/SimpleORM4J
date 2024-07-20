package net.skuratani.simpleorm4j.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.skuratani.simpleorm4j.annotation.Column;
import net.skuratani.simpleorm4j.annotation.Entity;
import net.skuratani.simpleorm4j.annotation.GenerateValue;
import net.skuratani.simpleorm4j.annotation.Id;
import net.skuratani.simpleorm4j.annotation.Table;

/**
 * <p>アノテーションに関するユーティリティクラス</p>
 * <pre>
 * {@link net.skuratani.simpleorm4j.annotation}で記述したアノテーションの存在確認などを実行する
 * ユーティリティクラス。
 * </pre>
 */
public class AnnotationUtil {

	/**
     * <p>Entityアノテーション確認</p>
     *
     * @param  clazz クラス
     * @return boolean
     *         true  : 存在する
     *         false : 存在しない
     */
	public static boolean hasEntityAnnotation(Class<?> clazz) {

		List<Annotation> annotationList = Stream.of(clazz.getDeclaredAnnotations())
										.filter(a -> a.annotationType().equals(Entity.class))
										.collect(Collectors.toList());
        return annotationList.isEmpty() ? false : true;
    }

	/**
     * <p>Columnアノテーション確認</p>
     *
     * @param  field フィールド
     * @return boolean
     *           true  : 存在する
     *           false : 存在しない
     */
	public static boolean hasColumnAnnotation(Field field) {
		List<Annotation> annotationList = Stream.of(field.getDeclaredAnnotations())
										.filter(a -> a.annotationType().equals(Column.class))
										.collect(Collectors.toList());
        return annotationList.isEmpty() ? false : true;
    }

	/**
     * <p>Tableアノテーション確認</p>
     *
     * @param  clazz クラス
     * @return boolean
     *           true  : 存在する
     *           false : 存在しない
     */
	public static boolean hasTableAnnotation(Class<?> clazz) {
		List<Annotation> annotationList = Stream.of(clazz.getDeclaredAnnotations())
										.filter(a -> a.annotationType().equals(Table.class))
										.collect(Collectors.toList());
        return annotationList.isEmpty() ? false : true;
    }

	/**
     * <p>Idアノテーション確認</p>
     *
     * @param  field フィールド
     * @return boolean
     *           true  : 存在する
     *           false : 存在しない
     */
	public static boolean hasIdAnnotation(Field field) {
		List<Annotation> annotationList = Stream.of(field.getDeclaredAnnotations())
												.filter(a -> a.annotationType().equals(Id.class))
												.collect(Collectors.toList());
        return annotationList.isEmpty() ? false : true;
    }

	/**
     * <p>GenerationValueアノテーション確認</p>
     *
     * @param  field フィールド
     * @return boolean
     *           true  : 存在する
     *           false : 存在しない
     */
	public static boolean hasGenerationValueAnnotation(Field field) {
		List<Annotation> annotationList = Stream.of(field.getDeclaredAnnotations())
												.filter(a -> a.annotationType().equals(GenerateValue.class))
												.collect(Collectors.toList());
        return annotationList.isEmpty() ? false : true;
    }

}
