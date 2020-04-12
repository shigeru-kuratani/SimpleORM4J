package net.skuratani.simpleorm4j.expression;

import java.lang.reflect.Field;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.skuratani.simpleorm4j.util.FieldUtil;

/**
 * <p>式クラス</p>
 * <pre>
 * Criteria APIで使用する式（Expression）を表現する。
 * (ex.) Expression.equal(School.class.getDeclaredField("id"), 10)
 *       → (SQL) SCHOOL.ID = 10
 * 本クラスで生成することができる式（Expression）は以下となります。
 *    1.  equal(=)：等価式
 *    2.  greaterThan(&gt;)：大なり式
 *    3.  lessThan(&lt;)：小なり式
 *    4.  greaterEqual(&gt;=)：大なりイコール式
 *    5.  lessEqual(&lt;=)：小なりイコール式
 *    6.  notEqual(&gt;&lt;)：不等価式
 *    7.  not(!)：否定式
 *    8.  isNull(IS NULL)：NULLイコール式
 *    9.  isNotNull(IS NOT NULL)：NULLノットイコール式
 *    10. between(BETWEEN A AND B)：範囲式
 *    11. in(IN (A, B, C))：IN式
 *    12. like(LIKE 'ZZZ')：LIKE式
 *    13. and(AND(Expression...))：論理積指定式
 *    14. or(OR((Expression...)))：論理和指定式
 *    15. asc(A ASC)：昇順式
 *    16. desc(A DESC)：降順式
 *    17. as(A AS ZZZ)：別名式
 *    18. count(COUNT(A))：カウント式
 *    19. sum(SUM(A))：合計式
 *    20. avg(AVG(A))：平均値式
 *    21. max(MAX(A))：最大値式
 *    22. min(MIN(A))：最小値式
 * </pre>
 *
 * @author  Shigeru Kuratani
 * @version 0.0.1
 */
public class Expression {

	/** 式文字列 */
	protected String _expressionStr;

	/**
	 * <p>コンストラクタ</p>
	 *
	 * @param expressionStr 式文字列
	 */
	protected Expression(String expressionStr) {
		_expressionStr = expressionStr;
	}

	/**
	 * <p>式文字列取得</p>
	 *
	 * @return 式文字列
	 */
	public String getExpressionStr() {
		return _expressionStr;
	}

	/**
	 * <p>式文字列設定</p>
	 *
	 * @param _expressionStr 設定する式文字列
	 */
	public void setExpressionStr(String _expressionStr) {
		this._expressionStr = _expressionStr;
	}

	/**
	 * <p>等価式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  obj   値
	 * @return 等価式
	 */
	public static Expression equal(Field field, Object obj) {
		return generateSimpleExpression(field, obj, "=");
	}

	/**
	 * <p>大なり式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  obj   値
	 * @return 大なり式
	 */
	public static Expression greaterThan(Field field, Object obj) {
		return generateSimpleExpression(field, obj, ">");
	}

	/**
	 * <p>小なり式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  obj   値
	 * @return 小なり式
	 */
	public static Expression lessThan(Field field, Object obj) {
		return generateSimpleExpression(field, obj, "<");
	}

	/**
	 * <p>大なりイコール式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  obj   値
	 * @return 大なりイコール式
	 */
	public static Expression greaterEqual(Field field, Object obj) {
		return generateSimpleExpression(field, obj, ">=");
	}

	/**
	 * <p>小なりイコール式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  obj   値
	 * @return 小なりイコール式
	 */
	public static Expression lessEqual(Field field, Object obj) {
		return generateSimpleExpression(field, obj, "<=");
	}

	/**
	 * <p>不等価式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  obj   値
	 * @return 不等価式
	 */
	public static Expression notEqual(Field field, Object obj) {
		return generateSimpleExpression(field, obj, "<>");
	}

	/**
	 * <p>NOT式</p>
	 *
	 * @param  expression 評価式
	 * @return NOT式
	 */
	public static Expression not(Expression expression) {
		return new Expression("NOT(" + expression.getExpressionStr() + ")");
	}

	/**
	 * <p>IS NULL式</p>
	 *
	 * @param  field エンティティフィールド
	 * @return IS NULL式
	 */
	public static Expression isNull(Field field) {
		return new Expression(FieldUtil.getFiledName(field) + " IS NULL ");
	}

	/**
	 * <p>IS NOT NULL式</p>
	 *
	 * @param  field エンティティフィールド
	 * @return IS NOT NULL式
	 */
	public static Expression isNotNull(Field field) {
		return new Expression(FieldUtil.getFiledName(field) + " IS NOT NULL ");
	}

	/**
	 * <p>BETWEEN式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  min   最小値
	 * @param  max   最大値
	 * @return BETWEEN式
	 */
	public static Expression between(Field field, Object min, Object max) {
		StringBuilder builder = new StringBuilder();
		builder.append(FieldUtil.getFiledName(field));
		builder.append(" BETWEEN ");
		builder.append(min instanceof String
					? ((String) min).matches(":[0-9a-zA-Z_]+") ? min.toString() : "'" + min.toString() + "'"
					: min.toString());
		builder.append(" AND ");
		builder.append(max instanceof String
					? ((String) max).matches(":[0-9a-zA-Z_]+") ? max.toString() : "'" + max.toString() + "'"
					: max.toString());
		return new Expression(builder.toString());
	}

	/**
	 * <p>IN式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  objs  値配列
	 * @return IN式
	 */
	public static Expression in(Field field, Object... objs) {
		StringBuilder builder = new StringBuilder();
		builder.append(FieldUtil.getFiledName(field));
		builder.append(" IN (");
		builder.append(Stream.of(objs).map(new Function<Object, String>(){
							@Override
							public String apply(Object o) {
								return o instanceof String
									? ((String) o).matches(":[0-9a-zA-Z_]+") ? o.toString() : "'" + o.toString() + "'"
									: o.toString();
							}
					   }).collect(Collectors.joining(",")));
		builder.append(")");
		return new Expression(builder.toString());
	}

	/**
	 * <p>LIKE式</p>
	 *
	 * @param  field エンティティフィールド
	 * @param  match マッチ文字列
	 * @return LIKE式
	 */
	public static Expression like(Field field, String match) {
		StringBuilder builder = new StringBuilder();
		builder.append(FieldUtil.getFiledName(field));
		builder.append(" LIKE ");
		builder.append(match.matches(":[0-9a-zA-Z_]+") ? match.toString() : "'" + match.toString() + "'");
		return new Expression(builder.toString());
	}

	/**
	 * <p>AND式</p>
	 * <pre>
	 * パラメータの式配列をパーレン（丸括弧）で括って、AND結合する。
	 * </pre>
	 *
	 * @param  expressions 式配列
	 * @return AND式
	 */
	public static Expression and(Expression... expressions) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(Stream.of(expressions).map(e -> e.getExpressionStr())
											 .collect(Collectors.joining(" AND ")));
		builder.append(")");
		return new Expression(builder.toString());
	}

	/**
	 * <p>OR式</p>
	 * <pre>
	 * パラメータの式配列をパーレン（丸括弧）で括って、OR結合する。
	 * </pre>
	 *
	 * @param  expressions 式配列
	 * @return OR式
	 */
	public static Expression or(Expression... expressions) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		builder.append(Stream.of(expressions).map(e -> e.getExpressionStr())
				 							 .collect(Collectors.joining(" OR ")));
		builder.append(")");
		return new Expression(builder.toString());
	}

	/**
	 * <p>ORDER BY句（ACCENDING）</p>
	 *
	 * @param  field       エンティティフィールド
	 * @return ORDER BY句（ACCENDING）
	 */
	public static Expression asc(Field field) {
		return new Expression(FieldUtil.getFiledName(field) + " ASC");
	}

	/**
	 * <p>ORDER BY句（DESCENDING）</p>
	 *
	 * @param  field       エンティティフィールド
	 * @return ORDER BY句（DESCENDING）
	 */
	public static Expression desc(Field field) {
		return new Expression(FieldUtil.getFiledName(field) + " DESC");
	}

	/**
	 * <p>別名式</p>
	 * <pre>
	 * Field or Expressionの情報から第2引数として指定された別名式を生成する。
	 * </pre>
	 *
	 * @param  obj       フィールド or Expression式
	 * @param  aliasName 別名
	 * @return 別名式
	 */
	public static Expression as(Object obj, String aliasName) {
		return new Expression((obj instanceof Field
							   ? FieldUtil.getFiledName((Field) obj)
							   : obj instanceof Expression
							  		? ((Expression) obj).getExpressionStr()
							  		: obj.toString())
							   + " AS " + aliasName);
	}

	/**
	 * <p>COUNT式</p>
	 *
	 * @param  obj カウント対象カラム（「*」or フィールド）
	 * @return COUNT式
	 */
	public static Expression count(Object obj) {
		if ("*".equals(obj)) {
			return new Expression("COUNT(*)");
		} else if (obj instanceof Field) {
			return new Expression("COUNT(" + FieldUtil.getFiledName((Field) obj) + ")");
		} else {
			return null;
		}
	}

	/**
	 * <p>SUM式</p>
	 *
	 * @param  field エンティティフィールド
	 * @return SUM式
	 */
	public static Expression sum(Field field) {
		return new Expression("SUM(" +  FieldUtil.getFiledName(field) + ")");
	}

	/**
	 * <p>AVG式</p>
	 *
	 * @param  field エンティティフィールド
	 * @return AVG式
	 */
	public static Expression avg(Field field) {
		return new Expression("AVG(" +  FieldUtil.getFiledName(field) + ")");
	}

	/**
	 * <p>MAX式</p>
	 *
	 * @param  field エンティティフィールド
	 * @return MAX式
	 */
	public static Expression max(Field field) {
		return new Expression("MAX(" +  FieldUtil.getFiledName(field) + ")");
	}

	/**
	 * <p>MIN式</p>
	 *
	 * @param  field エンティティフィールド
	 * @return MIN式
	 */
	public static Expression min(Field field) {
		return new Expression("MIN(" +  FieldUtil.getFiledName(field) + ")");
	}

	/**
	 * <p>シンプルな2項式を生成</p>
	 * <pre>
	 * シンプルな2項式を生成する。(ex.) MST_SCHOOL.ID &gt;= 10
	 * </pre>
	 *
	 * @param  field    エンティティフィールド
	 * @param  obj      値
	 * @param  operator 演算子
	 * @return 2項式
	 */
	protected static Expression generateSimpleExpression(Field field, Object obj, String operator) {

		StringBuilder builder = new StringBuilder();
		builder.append(FieldUtil.getFiledName(field));
		builder.append(" " + operator + " ");
		builder.append(obj instanceof String
					? ((String) obj).matches(":[0-9a-zA-Z_]+") ? obj.toString() : "'" + obj.toString() + "'"
					: obj instanceof Field ? FieldUtil.getFiledName((Field) obj)
										   : obj);

		return new Expression(builder.toString());
	}

}


