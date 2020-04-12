package net.skuratani.simpleorm4j.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.skuratani.simpleorm4j.vo.ConfigVO;

/**
 * <p>設定ファイル読み込みクラス</p>
 * <pre>
 * 設定ファイル（so4j.xml）からデータベース情報（DNS・URL・ユーザ・パスワードなど）の
 * 読み込みを実行するクラス。
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.1
 */
public class ConfigLoader {

	/** 設定ファイル名 */
	protected static final String CONFIG_FILE_NAME = "so4j.xml";

	/** 設定ファイルローダ */
	protected static ConfigLoader _configLoader = new ConfigLoader();

	/** 設定ファイルプロパティ */
	protected Properties _properties = new Properties();

	/**
	 * コンストラクタ
	 */
	protected ConfigLoader() {
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
			_properties.loadFromXML(inputStream);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe.getMessage(), ioe);
		}
	}

	/**
	 * <p>設定ファイルVO取得</p>
	 *
	 * @return 設定ファイルプロパティをマッピングした設定ファイルVO
	 */
	public static ConfigVO getConfig() {
		return ConfigVO.mapProps(_configLoader.getProperties());
	}

	/**
	 * <p>設定ファイルプロパティー取得</p>
	 *
	 * @return 設定ファイルプロパティ
	 */
	protected Properties getProperties() {
		return _properties;
	}

	/**
	 * <p>設定ファイルプロパティー取得</p>
	 *
	 * @return 設定ファイルプロパティ
	 */
	public static Properties getProps() {
		return _configLoader.getProperties();
	}
}
