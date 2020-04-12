package net.skuratani.simpleorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>エンティティテーブル アノテーション</p>
 * <pre>
 * エンティティクラスが対応するデータベーステーブル名をマッピングするアノテーションです。
 * name属性に指定したテーブルに対して、SELECT・INSERT・UPDATE・DELETEが実行されます。
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Table {
	String name();
}
