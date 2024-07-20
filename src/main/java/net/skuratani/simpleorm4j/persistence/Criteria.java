package net.skuratani.simpleorm4j.persistence;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.skuratani.simpleorm4j.expression.Expression;
import net.skuratani.simpleorm4j.type.JoinType;

/**
 * <p>Criteriaクラス</p>
 * <pre>
 * Criteria APIで発行するクエリの内容を表現するクラス。
 * {@link EntityManager#createQuery(Criteria)}の引数として使用し、データベースへ発行する
 * Queryインスタンスを生成する。
 * </pre>
 */
public class Criteria {

	/** 検索カラムリスト */
	protected List<?> _selectList;

	/** 検索テーブル */
	protected Class<?> _from;

	/** 結合テーブルリスト */
	protected List<Map<String, Object>> _joinTableList;

	/** WHERE式リスト */
	protected List<Expression> _whereExpressionList;

	/** GROUP BYリスト */
	protected List<Field> _groupByList;

	/** HAVINGリスト */
	protected List<Expression> _havingExpressionList;

	/** ORDER BYリスト */
	protected List<Expression> _orderByList;

	/** 登録テーブル */
	protected Class<?> _insert;

	/** value式リスト */
	protected List<Expression> _valueExpressionList;

	/** 更新テーブル */
	protected Class<?> _update;

	/** set式リスト */
	protected List<Expression> _setExpressionList;

	/** 削除テーブル */
	protected Class<?> _delete;

	/**
	 * コンストラクタ
	 */
	protected Criteria() {
		_joinTableList        = new ArrayList<>();
		_whereExpressionList  = new ArrayList<>();
		_groupByList          = new ArrayList<>();
		_havingExpressionList = new ArrayList<>();
		_orderByList          = new ArrayList<>();
		_valueExpressionList  = new ArrayList<>();
		_setExpressionList    = new ArrayList<>();
	}

	/**
	 * <p>クエリで取得するクラス・フィールドを指定</p>
	 * <pre>
	 * エンティティクラス、又は1つのエンティティクラスのフィールドを指定した場合は、
	 * そのエンティティクラスがリレーションデータのマッピングクラスとなる。
	 * 上記以外の場合（集約関数や複数のエンティティクラスのフィールドを指定した場合）は、
	 * マッピングクラスはMapとなる。
	 * </pre>
	 *
	 * @param  objects 取得クラス・フィールド配列
	 * @return Criteriaインスタンス
	 */
	public Criteria select(Object... objects) {
		_selectList = Arrays.asList(objects);
		return this;
	}

	/**
	 * <p>検索テーブル指定</p>
	 *
	 * @param  clazz 検索エンティティクラス
	 * @return Criteriaインスタンス
	 */
	public Criteria from(Class<?> clazz) {
		_from = clazz;
		return this;
	}

	/**
	 * <p>結合テーブル指定</p>
	 *
	 * @param  joinType       結合種別
	 * @param  joinClass      結合エンティティクラス
	 * @param  joinExpression 結合条件式
	 * @return Criteriaインスタンス
	 */
	public Criteria join(JoinType joinType, Class<?> joinClass, Expression joinExpression) {
		Map<String, Object> joinMap = new HashMap<>();
		joinMap.put("joinType", joinType);
		joinMap.put("joinClass", joinClass);
		joinMap.put("joinExpression", joinExpression);
		_joinTableList.add(joinMap);
		return this;
	}

	/**
	 * <p>WHERE条件設定</p>
	 * <pre>
	 * Expressionを使用してWHERE条件を設定する。
	 * 各条件はAND（論理積）で結合される。
	 * </pre>
	 *
	 * @param  whereExpressions WHERE条件式配列
	 * @return Criteriaインスタンス
	 */
	public Criteria where(Expression... whereExpressions) {
		_whereExpressionList = Arrays.asList(whereExpressions);
		return this;
	}

	/**
	 * <p>グルーピング条件設定</p>
	 * <pre>
	 * Fieldを使用してグルーピング条件を設定する。
	 * 各条件は設定順で条件設定される。
	 * </pre>
	 *
	 * @param  groupByFields グルーピングするフィールド配列
	 * @return Criteriaインスタンス
	 */
	public Criteria groupBy(Field... groupByFields) {
		_groupByList = Arrays.asList(groupByFields);
		return this;
	}

	/**
	 * <p>集約条件設定</p>
	 * <pre>
	 * Expressionを使用してHAVING条件を設定する。
	 * 各条件はAND（論理積）で結合される。
	 * </pre>
	 *
	 * @param  havingExpressions 集約検索条件式配列
	 * @return Criteriaインスタンス
	 */
	public Criteria having(Expression... havingExpressions) {
		_havingExpressionList = Arrays.asList(havingExpressions);
		return this;
	}

	/**
	 * <p>ソート条件設定</p>
	 * <pre>
	 * Expression（ASCENDING, DESCENDING）を使用してグルーピング条件を設定する。
	 * 各条件は設定順で条件設定される。
	 * </pre>
	 *
	 * @param  orderbyExpressions ソート条件式配列
	 * @return Criteriaインスタンス
	 */
	public Criteria orderyBy(Expression... orderbyExpressions) {
		_orderByList = Arrays.asList(orderbyExpressions);
		return this;
	}

	/**
	 * <p>登録テーブル指定</p>
	 *
	 * @param  clazz 登録エンティティクラス
	 * @return Criteriaインスタンス
	 */
	public Criteria insert(Class<?> clazz) {
		_insert = clazz;
		return this;
	}

	/**
	 * <p>VALUE式設定</p>
	 * <pre>
	 * {@link Expression#equal(Field, Object)}を使用して登録値を設定する。
	 * </pre>
	 *
	 * @param  valueExpressions VALUE値式配列
	 * @return Criteriaインスタンス
	 */
	public Criteria value(Expression... valueExpressions) {
		_valueExpressionList = Arrays.asList(valueExpressions);
		return this;
	}

	/**
	 * <p>更新テーブル指定</p>
	 *
	 * @param  clazz 更新エンティティクラス
	 * @return Criteriaインスタンス
	 */
	public Criteria update(Class<?> clazz) {
		_update = clazz;
		return this;
	}

	/**
	 * <p>更新式設定</p>
	 * <pre>
	 * {@link Expression#equal(Field, Object)}を使用して更新式を設定する。
	 * </pre>
	 *
	 * @param  setExpressions SET式配列
	 * @return Criteriaインスタンス
	 */
	public Criteria set(Expression... setExpressions) {
		_setExpressionList = Arrays.asList(setExpressions);
		return this;
	}

	/**
	 * <p>削除テーブル指定</p>
	 *
	 * @param  clazz 削除エンティティクラス
	 * @return Criteriaインスタンス
	 */
	public Criteria delete(Class<?> clazz) {
		_delete = clazz;
		return this;
	}

	/**
	 * <p>検索カラムリスト取得</p>
	 *
	 * @return 検索カラムリスト
	 */
	public List<?> getSelectList() {
		return _selectList;
	}

	/**
	 * <p>検索カラムリスト設定</p>
	 *
	 * @param selectList 検索カラムリスト
	 */
	public void setSelectList(List<?> selectList) {
		this._selectList = selectList;
	}

	/**
	 * <p>検索テーブル取得</p>
	 *
	 * @return 検索テーブル
	 */
	public Class<?> getFrom() {
		return _from;
	}

	/**
	 * <p>検索テーブル設定</p>
	 *
	 * @param from 検索テーブル
	 */
	public void setFrom(Class<?> from) {
		this._from = from;
	}

	/**
	 * <p>結合テーブルリスト取得</p>
	 *
	 * @return 結合テーブルリスト
	 */
	public List<Map<String, Object>> getJoinTableList() {
		return _joinTableList;
	}

	/**
	 * <p>結合テーブルリスト設定</p>
	 *
	 * @param joinTableList 結合テーブルリスト
	 */
	public void setJoinTableList(List<Map<String, Object>> joinTableList) {
		this._joinTableList = joinTableList;
	}

	/**
	 * <p>WHERE句リスト取得</p>
	 *
	 * @return WHERE句リスト
	 */
	public List<Expression> getWhereExpressionList() {
		return _whereExpressionList;
	}

	/**
	 * <p>WHERE句リスト設定</p>
	 *
	 * @param whereExpressionList WHERE句リスト
	 */
	public void setWhereExpressionList(List<Expression> whereExpressionList) {
		this._whereExpressionList = whereExpressionList;
	}

	/**
	 * <p>GROUP BY句リスト取得</p>
	 *
	 * @return GROUP BY句リスト
	 */
	public List<Field> getGroupByList() {
		return _groupByList;
	}

	/**
	 * <p>GROUP BY句リスト設定</p>
	 *
	 * @param groupByList GROUP BY句リスト
	 */
	public void setGroupByList(List<Field> groupByList) {
		this._groupByList = groupByList;
	}

	/**
	 * <p>HAVING句リスト取得</p>
	 *
	 * @return HAVING句リスト
	 */
	public List<Expression> getHavingExpressionList() {
		return _havingExpressionList;
	}

	/**
	 * <p>HAVING句リスト設定</p>
	 *
	 * @param havingExpressionList HAVING句リスト
	 */
	public void setHavingExpressionList(List<Expression> havingExpressionList) {
		this._havingExpressionList = havingExpressionList;
	}

	/**
	 * <p>ORDER BY句リスト取得</p>
	 *
	 * @return ORDER BY句リスト
	 */
	public List<Expression> getOrderByList() {
		return _orderByList;
	}

	/**
	 * <p>ORDER BY句リスト設定</p>
	 *
	 * @param orderByList ORDER BY句リスト
	 */
	public void setOrderByList(List<Expression> orderByList) {
		this._orderByList = orderByList;
	}

	/**
	 * <p>登録テーブル取得</p>
	 *
	 * @return 登録テーブル
	 */
	public Class<?> getInsert() {
		return _insert;
	}

	/**
	 * <p>登録テーブル設定</p>
	 *
	 * @param insert 登録テーブル
	 */
	public void setInsert(Class<?> insert) {
		this._insert = insert;
	}

	/**
	 * <p>value式リスト取得</p>
	 *
	 * @return value式リスト
	 */
	public List<Expression> getValueExpressionList() {
		return _valueExpressionList;
	}

	/**
	 * <p>value式リスト設定</p>
	 *
	 * @param valueExpressionList value式リスト
	 */
	public void setValueExpressionList(List<Expression> valueExpressionList) {
		this._valueExpressionList = valueExpressionList;
	}

	/**
	 * <p>更新テーブル取得</p>
	 *
	 * @return 更新テーブル
	 */
	public Class<?> getUpdate() {
		return _update;
	}

	/**
	 * <p>更新テーブル設定</p>
	 *
	 * @param update 更新テーブル
	 */
	public void setUpdate(Class<?> update) {
		this._update = update;
	}

	/**
	 * <p>set式リスト取得</p>
	 *
	 * @return set式リスト
	 */
	public List<Expression> getSetExpressionList() {
		return _setExpressionList;
	}

	/**
	 * <p>set式リスト設定</p>
	 *
	 * @param setExpressionList set式リスト
	 */
	public void setSetExpressionList(List<Expression> setExpressionList) {
		this._setExpressionList = setExpressionList;
	}

	/**
	 * <p>削除テーブル取得</p>
	 *
	 * @return 削除テーブル
	 */
	public Class<?> getDelete() {
		return _delete;
	}

	/**
	 * <p>削除テーブル設定</p>
	 *
	 * @param delete 削除テーブル
	 */
	public void setDelete(Class<?> delete) {
		this._delete = delete;
	}

}


