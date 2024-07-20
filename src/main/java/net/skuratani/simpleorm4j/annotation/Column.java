package net.skuratani.simpleorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>エンティティカラム アノテーション</p>
 * <pre>
 * フィールドが対応するデータベーステーブルのカラム名をマッピングするアノテーションです。
 * name属性に指定したカラムに対して、SELECT・INSERT・UPDATEが実行されます。
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
	String name();
}
