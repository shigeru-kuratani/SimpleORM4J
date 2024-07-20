package net.skuratani.simpleorm4j.vo;

import java.util.Properties;

/**
 * <p>設定ファイルバリューオブジェクト</p>
 * <pre>
 * 設定ファイルで定義された各プロパティを格納するValue Objectクラス。
 * </pre>
 */
public class ConfigVO {

	/** 定数：データソース名 */
	public static final String DSN = "dsn";

	/** 定数：データベースURL */
	public static final String URL = "url";

	/** 定数：データベースユーザ */
	public static final String USER = "user";

	/** 定数：データベースパスワード */
	public static final String PASSWORD = "password";

	/** 定数：オートコミット */
	public static final String AUTO_COMMIT = "autoCommit";

	/** 定数：トランザクション分離レベル */
	public static final String TRANSACTION_ISOLATION = "transactionIsolation";

	/** 定数：冗長モード */
	public static final String VERBOSE = "verbose";

	/** データソース名 */
	protected String dsn;

	/** データベースURL */
	protected String url;

	/** データベースユーザ名 */
	protected String user;

	/** データベースパスワード */
	protected String password;

	/** オートコミット */
	protected boolean autoCommit;

	/** トランザクション分離レベル */
	protected int transactionIsolation;

	/** 冗長モード */
	protected boolean verbose;

	/**
	 * <p>データソース名取得</p>
	 *
	 * @return データソース名
	 */
	public String getDsn() {
		return dsn;
	}

	/**
	 * <p>データソース名設定</p>
	 *
	 * @param dsn データソース名
	 */
	public void setDsn(String dsn) {
		this.dsn = dsn;
	}

	/**
	 * <p>url取得</p>
	 *
	 * @return URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * <p>url設定</p>
	 *
	 * @param url URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * <p>データベースユーザ名取得</p>
	 *
	 * @return データベースユーザ名
	 */
	public String getUser() {
		return user;
	}

	/**
	 * <p>データベースユーザ名設定</p>
	 *
	 * @param user データベースユーザ名
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * <p>データベースパスワード取得</p>
	 *
	 * @return データベースパスワード
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * <p>データベースパスワード設定</p>
	 *
	 * @param password データベースパスワード
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * <p>オートコミット取得</p>
	 *
	 * @return オートコミット値
	 */
	public boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 * <p>オートコミット設定</p>
	 *
	 * @param autoCommit オートコミット値
	 */
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * <p>トランザクション分離レベル取得</p>
	 *
	 * @return トランザクション分離レベル
	 */
	public int getTransactionIsolation() {
		return transactionIsolation;
	}

	/**
	 * <p>トランザクション分離レベル設定</p>
	 *
	 * @param transactionIsolation トランザクション分離レベル
	 */
	public void setTransactionIsolation(int transactionIsolation) {
		this.transactionIsolation = transactionIsolation;
	}

	/**
	 * <p>冗長モード取得</p>
	 *
	 * @return 冗長モード
	 */
	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * <p>冗長モード設定</p>
	 *
	 * @param verbose 冗長モード
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * <p>設定ファイルマッピング</p>
	 *
	 * @param  props プロパティ
	 * @return プロパティをマッピングした設定ファイルVO
	 */
	public static ConfigVO mapProps(Properties props) {

		ConfigVO cvo = new ConfigVO();
		cvo.setDsn(props.getProperty(DSN));
		cvo.setUrl(props.getProperty(URL));
		cvo.setUser(props.getProperty(USER));
		cvo.setPassword(props.getProperty(PASSWORD));
		cvo.setAutoCommit(Boolean.valueOf(props.getProperty(AUTO_COMMIT)));
		if (props.getProperty(TRANSACTION_ISOLATION) != null) {
			cvo.setTransactionIsolation(Integer.valueOf(props.getProperty(TRANSACTION_ISOLATION)));
		}
		cvo.setVerbose(Boolean.valueOf(props.getProperty(VERBOSE)));

		return cvo;
	}
}
