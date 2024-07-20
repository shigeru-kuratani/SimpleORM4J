package net.skuratani.simpleorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.skuratani.simpleorm4j.type.GenerationType;

/**
 * <p>値自動生成 アノテーション</p>
 * <pre>
 * シーケンス・オートインクリメントで値を自動生成する場合に、採番種別を設定するアノテーションです。
 * 1. シーケンスで値を生成する場合
 * 　　strategy（= GenerationType.SEQUENCE）・sequence両方を指定します。
 * 2. オートインクリメントで値を生成する場合
 *     strategy（= GenerationType.AUTO）のみ指定します。
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GenerateValue {
	GenerationType strategy();
	String sequence() default "";
}
