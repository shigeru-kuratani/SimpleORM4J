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
 *
 * @author  Shigeru Kuratani
 * @version 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Entity {}
