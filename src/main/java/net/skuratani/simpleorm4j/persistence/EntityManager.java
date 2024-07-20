package net.skuratani.simpleorm4j.persistence;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import net.skuratani.simpleorm4j.builder.QueryBuilder;
import net.skuratani.simpleorm4j.exception.So4jException;
import net.skuratani.simpleorm4j.expression.Expression;
import net.skuratani.simpleorm4j.io.StandartOutput;
import net.skuratani.simpleorm4j.loader.ConfigLoader;
import net.skuratani.simpleorm4j.util.AnnotationUtil;
import net.skuratani.simpleorm4j.vo.ConfigVO;

/**
 * <p>エンティティマネージャクラス</p>
 * <pre>
 * データベースと対話するために使用されるクラス。
 * データベースに対する一意なコネクションを管理する。
 * （エンティティマネージャが異なれば、データベースへのコネクションも異なる）
 * クエリ内容を表現したCriteriaから、データベースに発行するクエリ（Query）を生成する。
 * </pre>
 */
public class EntityManager {

	/** データベース接続 */
	protected Connection _connection;

	/**
	 * <p>コンストラクタ</p>
	 */
	protected EntityManager() {
		try {
			ConfigVO config = ConfigLoader.getConfig();
			// データソース指定の場合
			if (ConfigLoader.getProps().getProperty(ConfigVO.DSN) != null) {
				Context ctx   = new InitialContext();
				DataSource ds;
				try {
					ds = (DataSource)ctx.lookup(config.getDsn());
				} catch (NamingException e) {
					ds = (DataSource)ctx.lookup("java:comp/env/" + config.getDsn());
				}
				_connection   = ds.getConnection();
			// URL指定の場合
			} else if (ConfigLoader.getProps().getProperty(ConfigVO.URL) != null) {
				if (   ConfigLoader.getProps().getProperty(ConfigVO.USER) != null
					&& ConfigLoader.getProps().getProperty(ConfigVO.PASSWORD) != null) {
					_connection = DriverManager.getConnection(
							config.getUrl(), config.getUser(), config.getPassword());
				} else {
					_connection = DriverManager.getConnection(config.getUrl());
				}
			}
			// オートコミットモード指定
			if (ConfigLoader.getProps().getProperty(ConfigVO.AUTO_COMMIT) != null) {
				_connection.setAutoCommit(config.isAutoCommit());
			}
			// トランザクション分離レベル指定
			if (ConfigLoader.getProps().getProperty(ConfigVO.TRANSACTION_ISOLATION) != null) {
				_connection.setTransactionIsolation(config.getTransactionIsolation());
			}

			// デバッグ情報
			if (config.isVerbose()) {
				if (ConfigLoader.getProps().getProperty(ConfigVO.DSN) != null) {
					StandartOutput.writeln("SimpleORM4J : connect database : " + config.getDsn());
				} else {
					StandartOutput.writeln("SimpleORM4J : connect database : " + config.getUrl());
				}
			}

		} catch (SQLException | NamingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * <p>トランザクション開始</p>
	 * <pre>
	 * エンティティマネージャが有するデータベースコネクションのトランザクションを開始する。
	 * </pre>
	 */
	public void beginTransaction() {
		try {
			_connection.setAutoCommit(false);
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		}
	}

	/**
	 * <p>コミット</p>
	 * <pre>
	 * エンティティマネージャが有するデータベースコネクションのトランザクションをコミットする。
	 * </pre>
	 */
	public void commit() {
		try {
			_connection.commit();
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		}
	}

	/**
	 * <p>ロールバック</p>
	 * <pre>
	 * エンティティマネージャが有するデータベースコネクションのトランザクションをロールバックする。
	 * </pre>
	 */
	public void rollback() {
		try {
			_connection.rollback();
		} catch (SQLException sqle) {
			throw new RuntimeException(sqle.getMessage(), sqle);
		}
	}

	/**
	 * <p>Criteriaインスタンス取得</p>
	 *
	 * @return Criteriaインスタンス
	 */
	public Criteria getCriteria() {
		return new Criteria();
	}

	/**
	 * <p>Queryインスタンス生成</p>
	 * <pre>
	 * Criteria情報からQueryインスタンスを生成する。
	 * </pre>
	 *
	 * @param  criteria Criteriaインスタンス
	 * @return Queryインスタンス
	 */
	public Query createQuery(Criteria criteria) {
		QueryBuilder queryBuilder = new QueryBuilder(criteria);
		Query query = new Query(
					queryBuilder.judgeQueryType(),
					queryBuilder.createSql(),
					queryBuilder.judgeEntityClass(),
					_connection
				);

		// デバッグ情報
		if (ConfigLoader.getConfig().isVerbose()) {
			StandartOutput.writeln("SimpleORM4J : create query : " + System.lineSeparator() + query.getSql());
		}

		return query;
	}

	/**
	 * <p>Queryインスタンス生成</p>
	 * <pre>
	 *  SQL文からQueryインスタンスを生成する。
	 * </pre>
	 *
	 * @param  sql         SQL文
	 * @param  entityClass エンティティクラス
	 * @return Queryインスタンス
	 */
	public Query createRawQuery(String sql, Class<?> entityClass) {
		QueryBuilder queryBuilder = new QueryBuilder();
		return new Query(
				queryBuilder.judgeQueryType(sql),
				sql,
				entityClass,
				_connection
			);
	}

	/**
	 * <p>エンティティインスタンス登録</p>
	 * <pre>
	 * エンティティインスタンスから登録クエリを生成・実行して、引数のエンティティデータを
	 * データベースに登録する。
	 * </pre>
	 *
	 * @param  object エンティティインスタンス
	 * @return 登録件数
	 * @throws So4jException SQL文の実行に失敗した場合
	 */
	public int persist(Object object) throws So4jException {

		try {
			Class<?> clazz = object.getClass();
			Criteria criteria = new Criteria();
			criteria.insert(clazz);
			List<Object> paramList = new ArrayList<>();
			List<Expression> expressionList =
					  Stream.of(clazz.getDeclaredFields())
					  		.filter(f -> !AnnotationUtil.hasGenerationValueAnnotation(f))
							.map(new Function<Field, Expression>() {
								int i = 1;
								@Override
								public Expression apply(Field f) {
									try {
										PropertyDescriptor prop = new PropertyDescriptor(f.getName(), object.getClass());
										Method getter = prop.getReadMethod();
										Object value = getter.invoke(object, (Object[]) null);
										if (value instanceof Date || value instanceof LocalDate || value instanceof LocalDateTime) {
											paramList.add(value);
											return Expression.equal(f, String.valueOf(":" + i++));
										} else {
											return Expression.equal(f, value);
										}
									} catch (IntrospectionException | IllegalAccessException |
											IllegalArgumentException | InvocationTargetException e) {
										e.printStackTrace();
										return null;
									}
								}
							}).collect(Collectors.toList());
			criteria.value(expressionList.toArray(new Expression[0]));

			Query query = this.createQuery(criteria);
			for (int i = 1; i <= paramList.size(); i++) {
				query.setParameter(String.valueOf(i), paramList.get(i - 1));
			}
			return query.executeUpdate();

		} catch (Exception e) {
			throw new So4jException(e.getMessage(), e);
		}
	}

	/**
	 * <p>エンティティインスタンス更新</p>
	 * <pre>
	 * エンティティインスタンスから更新クエリを生成・実行して、引数のエンティティデータを更新する。
	 * </pre>
	 *
	 * @param  object エンティティインスタンス
	 * @return 更新件数
	 * @throws So4jException SQL文の実行に失敗した場合
	 */
	public int merge(Object object) throws So4jException {

		try {
			Class<?> clazz = object.getClass();
			Criteria criteria = new Criteria();
			criteria.update(clazz);
			List<Object> paramList = new ArrayList<>();
			List<Expression> setExpressionList =
					  Stream.of(clazz.getDeclaredFields())
					  		.filter(f -> !AnnotationUtil.hasIdAnnotation(f))
							.map(new Function<Field, Expression>() {
								int i = 1;
								@Override
								public Expression apply(Field f) {
									try {
										PropertyDescriptor prop = new PropertyDescriptor(f.getName(), object.getClass());
										Method getter = prop.getReadMethod();
										Object value = getter.invoke(object, (Object[]) null);
										if (value instanceof Date || value instanceof LocalDate || value instanceof LocalDateTime) {
											paramList.add(value);
											return Expression.equal(f, String.valueOf(":" + i++));
										} else {
											return Expression.equal(f, value);
										}
									} catch (IntrospectionException | IllegalAccessException |
											IllegalArgumentException | InvocationTargetException e) {
										e.printStackTrace();
										return null;
									}
								}
							}).collect(Collectors.toList());
			criteria.set(setExpressionList.toArray(new Expression[0]));

			List<Expression> whereExpressionList =
					Stream.of(clazz.getDeclaredFields())
						  .filter(f -> AnnotationUtil.hasIdAnnotation(f))
						  .map(new Function<Field, Expression>() {
							  @Override
								public Expression apply(Field f) {
									try {
										PropertyDescriptor prop = new PropertyDescriptor(f.getName(), object.getClass());
										Method getter = prop.getReadMethod();
										Object value = getter.invoke(object, (Object[]) null);
										return Expression.equal(f, value);
									} catch (IntrospectionException | IllegalAccessException |
											IllegalArgumentException | InvocationTargetException e) {
										e.printStackTrace();
										return null;
									}
								}
						  }).collect(Collectors.toList());
			criteria.where(whereExpressionList.toArray(new Expression[0]));

			Query query = this.createQuery(criteria);
			for (int i = 1; i <= paramList.size(); i++) {
				query.setParameter(String.valueOf(i), paramList.get(i - 1));
			}
			return query.executeUpdate();

		} catch (Exception e) {
			throw new So4jException(e.getMessage(), e);
		}
	}

	/**
	 * <p>エンティティインスタンス削除</p>
	 * <pre>
	 * エンティティインスタンスから削除クエリを生成・実行して、引数のエンティティデータを削除する。
	 * </pre>
	 *
	 * @param  object エンティティインスタンス
	 * @return 削除件数
	 * @throws So4jException SQL文の実行に失敗した場合
	 */
	public int remove(Object object) throws So4jException {

		try {
			Class<?> clazz = object.getClass();
			Criteria criteria = new Criteria();
			criteria.delete(clazz);
			List<Expression> whereExpressionList =
					Stream.of(clazz.getDeclaredFields())
						  .filter(f -> AnnotationUtil.hasIdAnnotation(f))
						  .map(new Function<Field, Expression>() {
							  @Override
								public Expression apply(Field f) {
									try {
										PropertyDescriptor prop = new PropertyDescriptor(f.getName(), object.getClass());
										Method getter = prop.getReadMethod();
										Object value = getter.invoke(object, (Object[]) null);
										return Expression.equal(f, value);
									} catch (IntrospectionException | IllegalAccessException |
											IllegalArgumentException | InvocationTargetException e) {
										e.printStackTrace();
										return null;
									}
								}
						  }).collect(Collectors.toList());
			criteria.where(whereExpressionList.toArray(new Expression[0]));

			Query query = this.createQuery(criteria);
			return query.executeUpdate();

		} catch (Exception e) {
			throw new So4jException(e.getMessage(), e);
		}
	}
}


