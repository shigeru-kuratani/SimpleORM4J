# SELECT

## 概要
このページでは、SimpleORM4Jを使用した検索処理（SELECT）に関してご説明をしていきます。  
リレーショナルデータベースへの検索処理で登場するクラスは以下になります。

<table>
    <tr>
        <th>EntityManagerFactory</th>
        <td>EntityManagerインスタンスを生成するファクトリクラスです。</td>
    </tr>
    <tr>
        <th>EntityManager</th>
        <td>
            データベースと対話するために使用されるクラスです。<br />
            リレーショナルデータベースへのコネクション毎に生成されます。
        </td>
    </tr>
    <tr>
        <th>Criteria</th>
        <td>発行するクエリの内容を表現するクラスです。</td>
    </tr>
    <tr>
        <th>Query</th>
        <td>リレーショナルデータベースに発行するクエリを表現するクラスです。</td>
    </tr>
</table>

また、SELECT処理で使用する「Criteria」クラスのメソッドは以下になります。

<table>
    <tr>
        <th>select</th>
        <td>検索するフィールド（カラム）を指定するメソッドです</td>
    </tr>
    <tr>
        <th>from</th>
        <td>検索するテーブルを指定するメソッドです。</td>
    </tr>
    <tr>
        <th>join</th>
        <td>結合テーブルとその結合方法、結合条件を指定するメソッドです。</td>
    </tr>
    <tr>
        <th>where</th>
        <td>検索条件（行条件）を指定するメソッドです。</td>
    </tr>
    <tr>
        <th>groupBy</th>
        <td>グルーピングするフィールド（カラム）を指定するメソッドです</td>
    </tr>
    <tr>
        <th>having</th>
        <td>検索条件（集約条件）を指定するメソッドです。</td>
    </tr>
    <tr>
        <th>orderBy</th>
        <td>ソート条件を指定するメソッドです。</td>
    </tr>
</table>

いくつかのメソッドを使用した処理の流れは以下になります。

```
try {
	// エンティティマネージャを生成
	EntityManager em = EntityManagerFactory.createEntityManager();
	// クライテリアを取得
	Criteria criteria = em.getCriteria();
	// クエリ内容の作成
	criteria.select(
				Department.class.getDeclaredField("id"),
				Department.class.getDeclaredField("name"),
				Department.class.getDeclaredField("schoolId"))
		  .from(Department.class)
		  .join(JoinType.LEFT, School.class,
			  Expression.equal(School.class.getDeclaredField("id"), Department.class.getDeclaredField("schoolId")))
		  .where(Expression.equal(School.class.getDeclaredField("id"), ":id"));
	// クエリを生成
	Query query = em.createQuery(criteria);
	// パラメータ設定
	query.setParameter("id", 1);
	// クエリ発行
	List<Department> resultList = (List<Department>) query.getResultList();
	System.out.println(resultList);
} catch (Exception e) {
	e.printStackTrace();
}
```

## クエリ内容の作成（Criteria）
SimpleORM4JでのSELECT処理で使用するCriteriaクラスのメソッドに関してご説明をしていきます。  
Criteriaで設定した内容がリレーショナルデータベースに問い合わせされます。  
また、Criteriaメソッドは自インスタンを返却しますので、メソッドチェーンでの指定が可能です。

### selectメソッド
検索フィールドを指定するメソッドです。  
指定できるものは以下になります。

- エンティティクラス
- エンティティクラスのフィールド
- 式表現（Expression）※ 集約関数式・別名式に限られます

```
■ エンティティクラスの指定
criteria.select(School.class)
```
```
■ エンティティクラスのフィールド指定例
criteria.select(
		Department.class.getDeclaredField("id"),
		Department.class.getDeclaredField("name"),
		Department.class.getDeclaredField("schoolId"))
```
```
■ 式表現（Expression）指定例
criteria.select(
		Expression.as(Expression.count("*"), "CNT"),
		Expression.as(Expression.count(School.class.getDeclaredField("id")), "CNT_SCHOOL"),
		Expression.as(Expression.sum(School.class.getDeclaredField("id")), "SUM_SCHOOL"),
		Expression.as(Expression.avg(School.class.getDeclaredField("id")), "AVG_SCHOOL"),
		Expression.as(Expression.max(Department.class.getDeclaredField("schoolId")), "MAX_SCHOOL"),
		Expression.as(Expression.min(Subject.class.getDeclaredField("departmentId")), "MIN_DEPARTMENT"))
```

リレーショナルデータベースのROWデータがマッピングされるクラスですが、selectメソッドでの指定によって判定がされます。

<table>
    <tr>
        <th>エンティティクラスのみ指定された場合</th>
        <td>指定されたエンティティクラスがマッピングクラスとなります。</td>
    </tr>
    <tr>
        <th>単一のエンティティクラスの複数フィールド（単一フィールド含む）指定の場合</th>
        <td>指定されたエンティティクラスがマッピングクラスとなります。</td>
    </tr>
    <tr>
        <th>上記以外</th>
        <td>Mapがマッピングクラスとなります。</td>
    </tr>
</table>

### fromメソッド
SELECT対象のテーブルを指定します。

```
■ 指定例
criteria.from(School.class)
```

### joinメソッド
joinメソッドは、結合テーブルとその結合方法、結合条件を指定します。

<table>
    <tr>
        <th>第1引数</th>
        <td>
            結合種別（JoinType列挙体）<br />
            1. INNER（内部結合）<br />
            2. LEFT（左外部結合）<br />
            3. RIGHT（右外部結合）<br />
            4. CROSS（クロス結合）
        </td>
    </tr>
    <tr>
        <th>第2引数</th>
        <td>結合テーブル（クラス）</td>
    </tr>
    <tr>
        <th>第3引数</th>
        <td>結合条件（Expression）</td>
    </tr>
</table>

```
■ 指定例
criteria.from(School.class)
    	  .join(JoinType.INNER, Department.class,
			  Expression.equal(School.class.getDeclaredField("id"), Department.class.getDeclaredField("schoolId")))
    	  .join(JoinType.INNER, Subject.class,
			  Expression.equal(Department.class.getDeclaredField("id"), Subject.class.getDeclaredField("departmentId")))
```

### whereメソッド
検索条件（行条件）を指定します。  
さまざまな式表現（Expression）を指定できます。  
詳細は、[式表現]()を参照ください。

```
■ 指定例（名前付きブレースホルダを使用）
criteria.where(Expression.between(School.class.getDeclaredField("id"), ":min", ":max"))
```

### groupByメソッド
グルーピングするフィールド（カラム）を指定します。

```
■ 指定例
criteria.groupBy(
		School.class.getDeclaredField("id"),
		Department.class.getDeclaredField("id"),
		Subject.class.getDeclaredField("id"))
```

### havingメソッド
集約したフィールド（カラム）に対する検索条件を指定します。

```
■ 指定例
criteria.having(Expression.greaterThan(Subject.class.getDeclaredField("id"), 30))
```

### orderByメソッド
ソート条件を指定します。

```
■ 指定例
criteria.orderyBy(
		Expression.asc(School.class.getDeclaredField("id")),
		Expression.desc(Department.class.getDeclaredField("id")))
```

### クエリメソッド
Criteriaの内容で生成されたクエリ（Query）のメソッドには以下があります

<table>
    <tr>
        <th>getSingleResult</th>
        <td>検索結果の最初の1件を取得します。</td>
    </tr>
    <tr>
        <th>getResultList</th>
        <td>検索結果の全件をリストで取得します</td>
    </tr>
</table>

```
■ 指定例
Query query = em.createQuery(criteria);
List<Department> resultList = (List<Department>) query.getResultList();
```
