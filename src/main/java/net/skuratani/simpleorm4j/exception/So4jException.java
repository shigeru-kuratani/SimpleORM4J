package net.skuratani.simpleorm4j.exception;

/**
 * <p>SimpleORM4J例外クラス</p>
 * <pre>
 * SimpleORM4Jでの処理中で例外が発生した場合に送出される例外です。
 * アプリケーションロジック内で本例外を補足して、以下を取得することができる。
 *    1. 例外メッセージ：So4jException.getMessage()
 *    2. 原因例外：So4jException.getCause()
 * </pre>
 */
public class So4jException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>コンストラクタ</p>
	 *
	 * @param message 例外メッセージ
	 * @param cause   原因例外
	 */
	public So4jException(String message, Throwable cause) {
		super(message, cause);
	}
}
