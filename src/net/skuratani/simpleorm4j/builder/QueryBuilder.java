package net.skuratani.simpleorm4j.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.skuratani.simpleorm4j.annotation.GenerateValue;
import net.skuratani.simpleorm4j.annotation.Table;
import net.skuratani.simpleorm4j.expression.Expression;
import net.skuratani.simpleorm4j.persistence.Criteria;
import net.skuratani.simpleorm4j.type.GenerationType;
import net.skuratani.simpleorm4j.type.JoinType;
import net.skuratani.simpleorm4j.type.QueryType;
import net.skuratani.simpleorm4j.util.AnnotationUtil;
import net.skuratani.simpleorm4j.util.FieldUtil;

/**
 * <p>クエリ生成クラス</p>
 * <pre>
 * Criteriaインスタンスからデータベースに発行するクエリを生成する処理を担当するクラスです。
 * エンティティマネージャは本クラスに実装されたメソッドを使用して以下の情報を生成し、
 * クエリインスタンスを生成します。
 *    1. クエリ種別
 *    2. SQL文
 *    3. マッピングエンティティクラス
 *    4. データベース接続
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.1
 */
public class QueryBuilder {

	/** Criteriaインスタンス */
	protected Criteria _criteria;

	/** クエリ種別 */
	protected QueryType _queryType;

	/** ラインフィード */
	private static final String LF = System.lineSeparator();

	/**
	 * <p>コンストラクタ</p>
	 */
	public QueryBuilder() {}

	/**
	 * <p>コンストラクタ</p>
	 *
	 * @param criteria Criteriaインスタンス
	 */
	public QueryBuilder(Criteria criteria) {
		_criteria = criteria;
	}

	/**
	 * <p>クエリ種別判定</p>
	 * <pre>
	 * Criteriaのクエリ情報からクエリ種別（SELECT・INSERT・UPDATE・DELETE）を判定する。
	 * </pre>
	 *
	 * @return クエリ種別
	 */
	public QueryType judgeQueryType() {

		if (_criteria.getSelectList() != null) {
			_queryType = QueryType.SELECT;
		} else if (_criteria.getInsert() != null) {
			_queryType = QueryType.INSERT;
		} else if (_criteria.getUpdate() != null) {
			_queryType = QueryType.UPDATE;
		} else if (_criteria.getDelete() != null) {
			_queryType = QueryType.DELETE;
		} else {
        	_queryType = null;
        }

		return _queryType;
	}

	/**
	 * <p>クエリ種別判定</p>
	 * <pre>
	 * SQL文からクエリ種別（SELECT・INSERT・UPDATE・DELETE）を判定する。
	 * </pre>
	 *
	 * @param  sql SQL文
	 * @return クエリ種別
	 */
	public QueryType judgeQueryType(String sql) {

		Pattern p = Pattern.compile("SELECT|INSERT|UPDATE|DELETE");
        Matcher m = p.matcher(sql);
        if (m.find()) {
        	if ("SELECT".equals(m.group())) {
        		_queryType = QueryType.SELECT;
        	} else if ("INSERT".equals(m.group())) {
        		_queryType = QueryType.INSERT;
        	} else if ("UPDATE".equals(m.group())) {
        		_queryType = QueryType.UPDATE;
        	} else if ("DELETE".equals(m.group())) {
        		_queryType = QueryType.DELETE;
        	}
        } else {
        	_queryType = null;
        }

		return _queryType;
	}

	/**
	 * <p>SQL文生成</p>
	 * <pre>
	 * Criteriaのクエリ情報からデータベースに発行するSQL文を生成する。
	 * </pre>
	 *
	 * @return SQL文
	 */
	public String createSql() {

		if (_queryType == QueryType.SELECT) {
			return this.generateSelectQuery();
		} else if (_queryType == QueryType.INSERT) {
			return this.generateInsertQuery();
		} else if (_queryType == QueryType.UPDATE) {
			return this.generateUpdateQuery();
		} else if (_queryType == QueryType.DELETE) {
			return this.generateDeleteQuery();
		}

		return "";
	}

	/**
	 * <p>エンティティクラス判定</p>
	 * <pre>
	 * Criteriaのクエリ情報から、ROWデータをマッピングするエンティティクラスを判定する。
	 * </pre>
	 *
	 * @return ROWデータをマッピングするエンティティクラス
	 */
	public Class<?> judgeEntityClass() {
		// SELECTの場合
		if (_queryType == QueryType.SELECT) {
			// エンティティークラスのみ指定の場合
			if (   _criteria.getSelectList().size() == 1
				&& _criteria.getSelectList().get(0) instanceof Class
				&& AnnotationUtil.hasEntityAnnotation((Class<?>) _criteria.getSelectList().get(0))) {
				return (Class<?>) _criteria.getSelectList().get(0);
			// 単一のエンティティクラスの複数フィールド（単一フィールド含む）指定の場合
			} else if (   this.isFieldAll(_criteria.getSelectList())
					   && this.isSameEntity(_criteria.getSelectList())
					   && AnnotationUtil.hasEntityAnnotation(((Field) _criteria.getSelectList().get(0)).getDeclaringClass())) {
				return ((Field) _criteria.getSelectList().get(0)).getDeclaringClass();
			// 上記以外
			} else {
				return Map.class;
			}
		}

		// INSERT・UPDATE・DELETEの場合
		return null;
	}

	/**
	 * <p>検索指定が全てフィールドか判定</p>
	 * <pre>
	 * {@link Criteria#select(Object...)}で指定された検索カラムが全てフィールドかを判定する。
	 * </pre>
	 *
	 * @param  selectList 指定検索カラムリスト
	 * @return boolean
	 *            true  : 全てフィールド
	 *            false : 上記以外
	 */
	protected boolean isFieldAll(List<?> selectList) {
		List<?> fieldList = selectList.stream()
									   .filter(o -> o instanceof Field)
									   .collect(Collectors.toList());
		return selectList.size() == fieldList.size() ? true : false;
	}

	/**
	 * <p>検索指定の全てフィールドか同じエンティティか判定</p>
	 * <pre>
	 * {@link Criteria#select(Object...)}で指定された検索カラムが全て同じエンティティクラスのフィールドかを判定する。
	 * </pre>
	 *
	 * @param  selectList 指定検索カラムリスト
	 * @return boolean
	 *            true  : 全て同じエンティティクラス
	 *            false : 上記以外
	 */
	protected boolean isSameEntity(List<?> selectList) {
		Set<?> fieldList = selectList.stream()
									  .map(o -> ((Field) o).getDeclaringClass())
									  .collect(Collectors.toSet());
		return fieldList.size() == 1 ? true : false;
	}

	/**
	 * <p>SELECT文生成</p>
	 * <pre>
	 * クエリ種別がSELECTの場合に、Criteriaのクエリ情報から発行するSELECT文を生成する。
	 * </pre>
	 *
	 * @return SELECT文
	 */
	protected String generateSelectQuery() {

		StringBuilder builder = new StringBuilder();
		builder.append("SELECT" + LF);
		builder.append(this.createSelectColumn() + LF);
		builder.append("FROM" + LF);
		builder.append(this.getFromTable() + LF);
		if (0 < _criteria.getJoinTableList().size()) {
			builder.append(this.createJoinExpression() + LF);
		}
		if (0 < _criteria.getWhereExpressionList().size()) {
			builder.append(this.createWhereExpression() + LF);
		}
		if (0 < _criteria.getGroupByList().size()) {
			builder.append(this.createGroupByExpression() + LF);
		}
		if (0 < _criteria.getHavingExpressionList().size()) {
			builder.append(this.createHavingExpression() + LF);
		}
		if (0 < _criteria.getOrderByList().size()) {
			builder.append(this.createOrderByExpression() + LF);
		}

		return builder.toString();
	}

	/**
	 * <p>検索するカラムリストを生成</p>
	 * <pre>
	 * Criteriaのクエリ情報（SELECTリスト）から、検索するカラムリストを生成する。
	 * </pre>
	 *
	 * @return 検索するカラムリスト
	 */
	protected String createSelectColumn() {
		// エンティティークラスのみ指定の場合
		if (   _criteria.getSelectList().size() == 1
			&& _criteria.getSelectList().get(0) instanceof Class
			&& AnnotationUtil.hasEntityAnnotation((Class<?>) _criteria.getSelectList().get(0))) {
			return this.getColumnsInClass((Class<?>) _criteria.getSelectList().get(0));
		// それ以外
		} else {
			return this.getColumns(_criteria.getSelectList());
		}
	}

	/**
	 * <p>検索カラムリストを取得</p>
	 * <pre>
	 * {@link Criteria#select(Object...)}で指定された検索カラムから、検索カラムリストを取得する。
	 * 指定する検索カラムは以下のクラスのみ許可される。
	 * 1. java.lang.reflect.Field
	 * 2. net.skuratani.simpleorm4j.expression.Expression
	 * ※ 直接SQL上のカラムを文字列で指定した場合は、空指定に変換される。
	 * </pre>
	 *
	 * @param  selectList 指定された検索カラムリスト
	 * @return 検索カラムリスト
	 */
	protected String getColumns(List<?> selectList) {
		return selectList.stream()
						 .map(new Function<Object, String>(){
							@Override
							public String apply(Object o) {
								if (o instanceof Field) {
									return FieldUtil.getFiledName((Field) o);
								} else if (o instanceof Expression) {
									return ((Expression) o).getExpressionStr();
								} else {
									return "";
								}
							}
						 }).collect(Collectors.joining(","));
	}

	/**
	 * <p>エンティティクラスから検索カラムリストを取得</p>
	 * <pre>
	 * CriteriaのSELECTリスト情報に設定されたエンティティクラスから、検索カラムリストを取得する。
	 * </pre>
	 *
	 * @param  clazz エンティティクラス
	 * @return エンティティクラスから抽出した検索カラムリスト
	 */
	protected String getColumnsInClass(Class<?> clazz) {
		return Stream.of(clazz.getDeclaredFields())
					 .map(f -> FieldUtil.getFiledName(f))
					 .collect(Collectors.joining(","));
	}

	/**
	 * <p>検索テーブル名取得</p>
	 * <pre>
	 * Criteriaのクエリ情報から、検索テーブル名を取得する。
	 * </pre>
	 *
	 * @return 検索テーブル名
	 */
	protected String getFromTable() {
		return AnnotationUtil.hasTableAnnotation(_criteria.getFrom())
					? ((Table) _criteria.getFrom().getDeclaredAnnotation(Table.class)).name()
					: _criteria.getFrom().getSimpleName();
	}

	/**
	 * <p>結合条件生成</p>
	 * <pre>
	 * Criteriaのクエリ情報から、テーブル結合条件式を生成する。
	 * </pre>
	 *
	 * @return 検索条件式
	 */
	protected String createJoinExpression() {
		return _criteria.getJoinTableList().stream()
						.map(new Function<Map<String, Object>, String>(){
							@Override
							public String apply(Map<String, Object> m) {
								StringBuilder builder = new StringBuilder();
								switch ((JoinType) m.get("joinType")) {
									case INNER:
										builder.append("INNER JOIN ");
										break;
									case LEFT:
										builder.append("LEFT OUTER JOIN ");
										break;
									case RIGHT:
										builder.append("RIGHT OUTER JOIN ");
										break;
									case CROSS:
										builder.append("CROSS JOIN ");
										break;
									default:
										builder.append("INNER JOIN ");
								}
								builder.append(this.getJoinTable((Class<?>) m.get("joinClass")));
								builder.append(LF);
								builder.append("ON ");
								builder.append(((Expression) m.get("joinExpression")).getExpressionStr());
								return builder.toString();
							}
							private String getJoinTable(Class<?> clazz) {
								return AnnotationUtil.hasTableAnnotation(clazz)
											? clazz.getDeclaredAnnotation(Table.class).name()
											: clazz.getSimpleName();
							}
						}).collect(Collectors.joining(LF));
	}

	/**
	 * <p>検索条件生成</p>
	 * <pre>
	 * Criteriaのクエリ情報から、検索条件式を生成する。
	 * </pre>
	 *
	 * @return 検索条件式
	 */
	protected String createWhereExpression() {
		return "WHERE" + LF +  _criteria.getWhereExpressionList().stream()
							   .map(e -> e.getExpressionStr())
							   .collect(Collectors.joining(" AND "));
	}

	/**
	 * <p>グルーピング条件生成</p>
	 * <pre>
	 * Criteriaのクエリ情報から、グルーピング条件式を生成する。
	 * </pre>
	 *
	 * @return グルーピング条件式
	 */
	protected String createGroupByExpression() {
		return "GROUP BY" + LF + _criteria.getGroupByList().stream()
								 .map(f -> FieldUtil.getFiledName(f))
								 .collect(Collectors.joining(","));
	}

	/**
	 * <p>集約検索条件生成</p>
	 * <pre>
	 * Criteriaのクエリ情報から、集約検索条件式を生成する。
	 * </pre>
	 *
	 * @return 検索条件式
	 */
	protected String createHavingExpression() {
		return "HAVING" + LF +  _criteria.getHavingExpressionList().stream()
							   .map(e -> e.getExpressionStr())
							   .collect(Collectors.joining(" AND "));
	}

	/**
	 * <p>ソート条件生成</p>
	 * <pre>
	 * Criteriaのクエリ情報から、ソート条件式を生成する。
	 * </pre>
	 *
	 * @return ソート条件式
	 */
	protected String createOrderByExpression() {
		return "ORDER BY" + LF + _criteria.getOrderByList().stream()
								 .map(e -> e.getExpressionStr())
								 .collect(Collectors.joining(","));
	}

	/**
	 * <p>INSERT文生成</p>
	 * <pre>
	 * クエリ種別がINSERTの場合に、Criteriaのクエリ情報から発行するINSERT文を生成する。
	 * </pre>
	 *
	 * @return INSERT文
	 */
	protected String generateInsertQuery() {

		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO " + this.getInsertTable() + LF);
		builder.append("(" + this.createInsertColumns() + ")" + LF);
		builder.append("VALUES" + LF);
		builder.append("(" + this.createInsertValues() + ")" + LF);

		return builder.toString();
	}

	/**
	 * <p>登録テーブル取得</p>
	 * <pre>
	 * Criteriaのクエリ情報から、登録テーブルを取得する。
	 * </pre>
	 *
	 * @return 検索テーブル名
	 */
	protected String getInsertTable() {
		return AnnotationUtil.hasTableAnnotation(_criteria.getInsert())
					? ((Table) _criteria.getInsert().getDeclaredAnnotation(Table.class)).name()
					: _criteria.getInsert().getSimpleName();
	}

	/**
	 * <p>登録カラム生成</p>
	 * <pre>
	 * Criteriaのクエリ情報から、登録カラムを生成する。
	 * </pre>
	 *
	 * @return 登録カラム
	 */
	protected String createInsertColumns() {

		String sequenceField = "";
		for (Field field : _criteria.getInsert().getDeclaredFields()) {
			if (AnnotationUtil.hasGenerationValueAnnotation(field)) {
				Annotation annotation = field.getDeclaredAnnotation(GenerateValue.class);
				sequenceField = ((GenerateValue) annotation).strategy() == GenerationType.SEQUENCE
								? FieldUtil.getFiledName(field) : "";
			}
		}

		return (!"".equals(sequenceField) ? sequenceField + "," : "") +
							_criteria.getValueExpressionList().stream()
							.map(new Function<Expression, String>() {
								@Override
								public String apply(Expression e) {
									if ("=".equals(e.getExpressionStr().split("\\s")[1])) {
										// 「テーブ名.カラム名」から「テーブル名.」を削除する（PostgreSQL対応）
										return e.getExpressionStr().split("\\s")[0].replaceFirst("[0-9a-zA-Z_\\-]+?\\.", "");
									} else {
										return "";
									}
								}
							 }).collect(Collectors.joining(","));
	}

	/**
	 * <p>登録値生成</p>
	 * <pre>
	 * Criteriaのクエリ情報から、登録値を生成する。
	 * </pre>
	 *
	 * @return 登録値
	 */
	protected String createInsertValues() {

		String sequence = "";
		for (Field field : _criteria.getInsert().getDeclaredFields()) {
			if (AnnotationUtil.hasGenerationValueAnnotation(field)) {
				Annotation annotation = field.getDeclaredAnnotation(GenerateValue.class);
				sequence = ((GenerateValue) annotation).strategy() == GenerationType.SEQUENCE
							? ((GenerateValue) annotation).sequence() + ".NEXTVAL" : "";
			}
		}

		return (!"".equals(sequence) ? sequence + "," : "") +
							 _criteria.getValueExpressionList().stream()
							 .map(new Function<Expression, String>() {
								@Override
								public String apply(Expression e) {
									if ("=".equals(e.getExpressionStr().split("\\s")[1])) {
										return e.getExpressionStr().split("\\s", 3)[2];
									} else {
										return "";
									}
								}
							 }).collect(Collectors.joining(","));
	}

	/**
	 * <p>UPDATE文生成</p>
	 * <pre>
	 * クエリ種別がUPDATEの場合に、Criteriaのクエリ情報から発行するUPDATE文を生成する。
	 * </pre>
	 *
	 * @return UPDATE文
	 */
	protected String generateUpdateQuery() {

		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE " + this.getUpdateTable() + LF);
		builder.append("SET " + this.createUpdateExpression() + LF);
		if (0 < _criteria.getWhereExpressionList().size()) {
			builder.append(this.createWhereExpression());
		}

		return builder.toString();
	}

	/**
	 * <p>更新テーブル取得</p>
	 * <pre>
	 * Criteriaのクエリ情報から、更新テーブルを取得する。
	 * </pre>
	 *
	 * @return 検索テーブル名
	 */
	protected String getUpdateTable() {
		return AnnotationUtil.hasTableAnnotation(_criteria.getUpdate())
					? ((Table) _criteria.getUpdate().getDeclaredAnnotation(Table.class)).name()
					: _criteria.getUpdate().getSimpleName();
	}

	/**
	 * <p>更新式生成</p>
	 * <pre>
	 * Criteriaのクエリ情報から、更新式を生成する。
	 * </pre>
	 *
	 * @return 更新式
	 */
	protected String createUpdateExpression() {
		return _criteria.getSetExpressionList().stream()
						.filter(e -> "=".equals(e.getExpressionStr().split("\\s")[1]))
						// 「テーブ名.カラム名」から「テーブル名.」を削除する（PostgreSQL対応）
						.map(e -> e.getExpressionStr().replaceFirst("[0-9a-zA-Z_\\-]+?\\.", ""))
						.collect(Collectors.joining(","));
	}

	/**
	 * <p>DELETE文生成</p>
	 * <pre>
	 * クエリ種別がDELETEの場合に、Criteriaのクエリ情報から発行するDELETE文を生成する。
	 * </pre>
	 *
	 * @return UPDATE文
	 */
	protected String generateDeleteQuery() {

		StringBuilder builder = new StringBuilder();
		builder.append("DELETE FROM " + this.getDeleteTable() + LF);
		if (0 < _criteria.getWhereExpressionList().size()) {
			builder.append(this.createWhereExpression());
		}

		return builder.toString();
	}

	/**
	 * <p>削除テーブル取得</p>
	 * <pre>
	 * Criteriaのクエリ情報から、削除テーブルを取得する。
	 * </pre>
	 *
	 * @return 検索テーブル名
	 */
	protected String getDeleteTable() {
		return AnnotationUtil.hasTableAnnotation(_criteria.getDelete())
					? ((Table) _criteria.getDelete().getDeclaredAnnotation(Table.class)).name()
					: _criteria.getDelete().getSimpleName();
	}

}


