package net.skuratani.simpleorm4j.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.skuratani.simpleorm4j.exception.So4jException;
import net.skuratani.simpleorm4j.io.StandartOutput;
import net.skuratani.simpleorm4j.loader.ConfigLoader;
import net.skuratani.simpleorm4j.mapper.EntityPropertyRowMapper;
import net.skuratani.simpleorm4j.mapper.IfRowMapper;
import net.skuratani.simpleorm4j.mapper.MapRowMapper;
import net.skuratani.simpleorm4j.type.QueryType;
import net.skuratani.simpleorm4j.util.AnnotationUtil;

/**
 * <p>データベースに発行するクエリを表現するクラス</p>
 * <pre>
 * {@link EntityManager#createQuery(Criteria)}でCriteria情報から作成する。
 * データベースへの問合せの他、名前付きプレースホルダへの値のバインドメソッドも外部に提供する。
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.3
 */
public class Query {

	/** クエリ種別 */
	protected QueryType _queryType;

	/** 発行SQL */
	protected String _sql;

	/** バインドパラメータリスト */
	protected List<Map<String, Object>> _paramList = new ArrayList<>();

	/** マッピングエンティティ */
	protected Class<?> _entityClass;

	/** コネクション */
	protected Connection _connection;

	/** プリペアドステートメント */
	protected PreparedStatement _preparedStatement;

	/** リザルトセット */
	protected ResultSet _resultSet;

	/** 検索結果リスト */
	List<Object> _resultList;

	/**
	 * <p>コンストラクタ</p>
	 *
	 * @param queryType   クエリ種別
	 * @param sql         発行SQL
	 * @param entityClass マッピングエンティティクラス
	 * @param connection  データベースコネクション
	 */
	public Query(QueryType queryType, String sql, Class<?> entityClass, Connection connection) {
		_queryType   = queryType;
		_sql         = sql;
		_entityClass = entityClass;
		_connection  = connection;
	}

	/**
	 * <p>検索結果（1件）取得</p>
	 * <pre>
	 * Criteriaの設定情報で生成したSQLを発行した検索結果を、エンティティにマッピングした最初の1件を返却する。
	 * </pre>
	 *
	 * @return 検索結果をエンティティにマッピングした1件データ
	 * 　　　　　検索結果が0件の場合はnullを返却
	 * @throws So4jException プリペアドステートメントの生成に失敗した場合<br>
	 *                       パラメータのバインド処理に失敗した場合<br>
	 *                       SQLの実行でエラーが発生した場合<br>
	 *                       エンティティインスタンスフィールドに値を設定出来なかった場合
	 */
	public Object getSingleResult() throws So4jException {
		List<?> resultList = this.selectQuery();
		return 0 < resultList.size() ? resultList.get(0) : null;
	}

	/**
	 * <p>検索結果リスト取得</p>
	 * <pre>
	 * Criteriaの設定情報で生成したSQLを発行した検索結果を、エンティティにマッピングしたリストを返却する。
	 * </pre>
	 *
	 * @return 検索結果をエンティティにマッピングしたリスト
	 * @throws So4jException プリペアドステートメントの生成に失敗した場合<br>
	 *                       パラメータのバインド処理に失敗した場合<br>
	 *                       SQLの実行でエラーが発生した場合<br>
	 *                       エンティティインスタンスフィールドに値を設定出来なかった場合
	 */
	public List<?> getResultList() throws So4jException {
		return this.selectQuery();
	}

	/**
	 * <p>検索メソッド</p>
	 *
	 * @return resultList 検索結果リスト
	 * @throws So4jException プリペアドステートメントの生成に失敗した場合<br>
	 *                       パラメータのバインド処理に失敗した場合<br>
	 *                       SQLの実行でエラーが発生した場合<br>
	 *                       エンティティインスタンスフィールドに値を設定出来なかった場合
	 */
	protected List<?> selectQuery() throws So4jException {

		try {
			// クエリ種別の判定
			if (_queryType != QueryType.SELECT) {
				throw new Exception("mismatch the type of executed query." + " query type : " + _queryType.toString());
			}

			// 名前付きプレースホルダを「?」に変更
			String sql = this.convertNamedPlaceholder(String.valueOf(_sql));

			// プリペアドステートメントを生成
			_preparedStatement = _connection.prepareStatement(sql);

			// パラメータのセット
			this.bindParameter();

			// SQL発行
			_resultSet = _preparedStatement.executeQuery();

			// マッピングするエンティティクラスの判定
			IfRowMapper<?> rowMapper;
			if (AnnotationUtil.hasEntityAnnotation(_entityClass)) {
				rowMapper = new EntityPropertyRowMapper<>(_entityClass);
			} else if ("Map".equals(_entityClass.getSimpleName())) {
				rowMapper = new MapRowMapper<>(Map.class);
			} else {
				return null;
			}

			// 結果セットからリストに変換
			_resultList = new ArrayList<Object>();
			while (_resultSet.next()) {
				_resultList.add(rowMapper.mapRow(_resultSet));
			}
		} catch (Exception e) {
			throw new So4jException(e.getMessage(), e);
		}

		// デバッグ情報
		if (ConfigLoader.getConfig().isVerbose()) {
			StandartOutput.writeln("SimpleORM4J : fetch resultset : count " + _resultList.size());
		}

		return _resultList;
	}


	/**
	 * <p>登録・更新・削除処理</p>
	 * <pre>
	 * Criteriaの設定情報で生成したSQLを実行し、その実行の登録・更新・削除件数を返却する。
	 * </pre>
	 *
	 * @return 登録・更新・削除件数
	 * @throws So4jException プリペアドステートメントの生成に失敗した場合<br>
	 *                       バインドパラメータの設定に失敗した場合<br>
	 *                       SQLの実行でエラーが発生した場合
	 */
	public int executeUpdate() throws So4jException {

		try {
			// クエリ種別の判定
			if (!(   _queryType == QueryType.INSERT
				  || _queryType == QueryType.UPDATE
				  || _queryType == QueryType.DELETE)) {
				throw new Exception("mismatch the type of executed query." + " query type : " + _queryType.toString());
			}

			// 名前付きプレースホルダを「?」に変更
			String sql = this.convertNamedPlaceholder(String.valueOf(_sql));

			// プリペアドステートメントを生成
			_preparedStatement = _connection.prepareStatement(sql);

			// パラメータのセット
			this.bindParameter();

			// SQL発行
			int updatedCount = _preparedStatement.executeUpdate();

			// デバッグ情報
			if (ConfigLoader.getConfig().isVerbose()) {
				StandartOutput.writeln("SimpleORM4J : execute Query#executeUpdate : count " + updatedCount);
			}

			return updatedCount;

		} catch (Exception e) {
			throw new So4jException(e.getMessage(), e);
		}
	}

	/**
	 * <p>SQLバインドパラメータ設定</p>
	 * <pre>
	 * 発行SQLのプレースホルダにバインドするパラメータを設定する。
	 * </pre>
	 *
	 * @param  paramName プレースホルダ名
	 * @param  value     プレースホルダにバインドする値
	 */
	public void setParameter(String paramName, Object value) {

		// 名前付きプレースホルダリスト生成
		List<String> paramList = new ArrayList<>();
		Pattern p = Pattern.compile(":([0-9a-zA-Z_]+)");
        Matcher m = p.matcher(_sql);
        while (m.find()) {
        	paramList.add(m.group(1));
        }

        // セットするプレースホルダのインデックスを取得
        int setIndex = 1;
        for (String param : paramList) {
        	if (paramName.equals(param)) break;
        	setIndex++;
        }

        // パラメータリストに追加
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(paramName, value);
        paramMap.put("setIndex", setIndex);
        _paramList.add(paramMap);
	}

	/**
	 * <p>発行SQLの名前付きプレースホルダを「?」に変更</p>
	 *
	 * @param  sql 発行SQL文（インスタンスフィールドを複製した文字列）
	 * @return 名前付きプレースホルダを「?」に変更したSQL文
	 */
	protected String convertNamedPlaceholder(String sql) {
		for (Map<String, Object> paramMap : _paramList) {
			for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
				if (!"setIndex".equals(entry.getKey())) {
					sql = sql.replace(":" + entry.getKey(), "?");
				}
			}
		}
		return sql;
	}

	/**
	 * <p>パラメータバインド</p>
	 * <pre>
	 * {@link Query#setParameter(String, Object)}で設定したパラメータをプレースホルダにバインドする。
	 * </pre>
	 *
	 * @throws So4jException パラメータのバインドに失敗した場合
	 */
	protected void bindParameter() throws So4jException {

		try {
			for (Map<String, Object> paramMap : _paramList) {
				int i = 1;
				Object obj = null;
				for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
					if ("setIndex".equals(entry.getKey())) {
						i = (int) entry.getValue();
					} else {
						obj = entry.getValue();
					}
				}
				// プレースホルダへのバインド
				if (obj == null) {
					_preparedStatement.setString(i, "");
				} else if (obj instanceof String) {
					_preparedStatement.setString(i, (String) obj);
				} else if (obj instanceof Long) {
					_preparedStatement.setLong(i, (Long) obj);
				} else if (obj instanceof Integer) {
					_preparedStatement.setInt(i, (Integer) obj);
				} else if (obj instanceof Double) {
					_preparedStatement.setDouble(i, (Double) obj);
				} else if (obj instanceof Float) {
					_preparedStatement.setFloat(i, (Float) obj);
				} else if (obj instanceof java.util.Date) {
					_preparedStatement.setTimestamp(i, new java.sql.Timestamp(((java.util.Date) obj).getTime()));
				} else if (obj instanceof java.time.LocalDate) {
					LocalDate dateTime = (java.time.LocalDate) obj;
					ZoneId zone = ZoneId.systemDefault();
					ZonedDateTime zonedDateTime = ZonedDateTime.of(
							LocalDateTime.of(
									dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), 0, 0, 0), zone);
					_preparedStatement.setTimestamp(i, new java.sql.Timestamp(zonedDateTime.toInstant().toEpochMilli()));
				} else if (obj instanceof java.time.LocalDateTime) {
					ZoneId zone = ZoneId.systemDefault();
					ZonedDateTime zonedDateTime = ZonedDateTime.of((java.time.LocalDateTime) obj, zone);;
					_preparedStatement.setTimestamp(i, new java.sql.Timestamp(zonedDateTime.toInstant().toEpochMilli()));
				} else {
					_preparedStatement.setObject(i, obj);
				}
			}
		} catch (SQLException sqle) {
			throw new So4jException(sqle.getMessage(), sqle);
		}
	}

	/**
	 * <p>発行SQL取得</p>
	 * <pre>
	 * データベースに発行するSQLを取得する。
	 * </pre>
	 *
	 * @return SQL文
	 */
	public String getSql() {
		return this._sql;
	}
}
