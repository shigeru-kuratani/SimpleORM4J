package net.skuratani.simpleorm4j.type;

/**
 * <p>クエリ種別</p>
 * <pre>
 * データベースに発行するクエリの種別を表す。
 *    1. SELECT
 *    2. INSERT
 *    3. UPDATE
 *    4. DELETE
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.1
 */
public enum QueryType {
	SELECT, INSERT, UPDATE, DELETE
}
