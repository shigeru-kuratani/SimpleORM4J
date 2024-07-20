package net.skuratani.simpleorm4j.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>主キーアノテーション</p>
 * <pre>
 * 主キーを識別する為のマーカーアノテーションです。
 * 複数のカラムで一意になる複合主キーの場合は、その複数カラムに対応するフィールドに
 * Idアノテーションを記述します。
 * エンティティインスタンスを指定して実行するUPDATE（更新）・DELETE（削除）処理において
 * リレーションデータを一意に特定する為に@Idアノテーションが使用されます。
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Id {}
