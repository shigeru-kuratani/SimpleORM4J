package net.skuratani.simpleorm4j.mapper;

import java.sql.ResultSet;

import net.skuratani.simpleorm4j.exception.So4jException;

/**
 * <p>ROWマッパインターフェイス</p>
 * <pre>
 * 各ROWマッパーで実装するインターフェイスを定義する。
 * </pre>
 */
public interface IfRowMapper<T> {

	/**
	 * <p>ROWマップ処理</p>
	 *
	 * @param  resultSet リザルトセット
	 * @return マップ済みインスタンス
	 * @throws So4jException SimpleORM4J例外
	 */
	public T mapRow(ResultSet resultSet) throws So4jException;
}
