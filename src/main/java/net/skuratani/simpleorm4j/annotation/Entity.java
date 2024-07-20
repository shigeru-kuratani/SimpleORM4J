package net.skuratani.simpleorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>エンティティアノテーション</p>
 * <pre>
 * このアノテーションが記述されたクラスはエンティティとして認識されます。
 * エンティティを識別する為のマーカーアノテーションです。
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {}
