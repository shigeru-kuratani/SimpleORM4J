package net.skuratani.simpleorm4j.persistence;

/**
 * <p>エンティティマネージャ生成クラス</p>
 * <pre>
 * エンティティマネージャの生成を実行する。
 * </pre>
 */
public class EntityManagerFactory {

	/**
	 * コンストラクタ
	 */
	protected EntityManagerFactory() {}

	/**
	 * エンティティマネージャ生成
	 *
	 * @return エンティティマネージャインスタンス
	 */
	public static EntityManager createEntityManager() {
		return new EntityManager();
	}
}
