package net.skuratani.simpleorm4j.type;

/**
 * <p>テーブル結合種別</p>
 * <pre>
 * テーブル結合種別を表す。
 *    1. INNER
 *    2. LEFT
 *    3. RIGHT
 *    4. CROSS
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.1
 */
public enum JoinType {
	INNER, LEFT, RIGHT, CROSS
}
