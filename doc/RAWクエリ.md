# RAWクエリ

## 概要
SimpleORM4Jでは、Criteria APIでは表現できない複雑なクエリや、各リレーショナルデータベース独自の仕様を使ったSQLを実行する為のインターフェイスを用意しています。

```
■ EntityManager#createRawQueryシグネチャ
public Query createRawQuery(String sql, Class entityClass)
```

<table>
    <tr>
        <th>第1引数</th>
        <td>SQL文（RAWクエリ）</td>
    </tr>
    <tr>
        <th>第2引数</th>
        <td>
            マッピングクラス（このクラスにSQL実行結果がマッピングされます）<br />
            ※ Map.classを指定するとHashMapにマッピングされます。
        </td>
    </tr>
</table>

## 実装例
RAWクエリ（生SQL）の実装例を以下に記載をします。

### 実装例1
Oracle Database固有の「ヒント句」を使用した実装例です。  
また、SimpleORM4JのCriteria APIでは実現できない「サブクエリ」を使用しています。

```
■ 実装例1
try {
	EntityManager em = EntityManagerFactory.createEntityManager();
	String sql = """
            SELECT
            /*+ INDEX(DM PK_MST_DEPARTMENT) */
                DM.ID, DM.NAME
            FROM
                MST_DEPARTMENT DM
            INNER JOIN
                (SELECT
                    MAX(MST_DEPARTMENT.ID) AS MAX_ID
                FROM
                    MST_DEPARTMENT
                GROUP BY
                    MST_DEPARTMENT.SCHOOL_ID) DM_MAX
            ON
                DM_MAX.MAX_ID = DM.ID
            """;
	Query query = em.createRawQuery(sql, Department.class);
	List<Department> resultList = (List<Department>) query.getResultList();
	System.out.println(EntityResultViewHelper.generateTableView(resultList));
} catch (Exception e) {
	e.printStackTrace();
}
```

### 実装例2
Oracle Databaseでのみ正常に実行できる更新文です。   
SQLの実行は更新系クエリですので、Query#executeUpdateメソッドを使用します。

```
■ 実装例2
try {
	EntityManager em = EntityManagerFactory.createEntityManager();
	String sql = """
            UPDATE
		      (SELECT
		          MST_CHARGE.NAME
		        FROM
		          MST_CHARGE
		        WHERE   
		          MST_CHARGE.ID = 4) C
            SET C.NAME = '担当者D:更新RAWクエリ'
            """;
	Query query = em.createRawQuery(sql, null);
	int updCnt = query.executeUpdate();
	System.out.println("Update count : " + updCnt);
} catch (Exception e) {
	e.printStackTrace();
}
```
