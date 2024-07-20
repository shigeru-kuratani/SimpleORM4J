package net.skuratani.simpleorm4j.type;

/**
 * <p>自動採番種別</p>
 * <pre>
 * 自動採番種別を表す。
 *    1. SEQUENCE：シーケンス（順序オブジェクト）で採番
 *    2. AUTO：オートインクリメント採番
 * </pre>
 */
public enum GenerationType {
	SEQUENCE, AUTO
}
