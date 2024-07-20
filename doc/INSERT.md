# INSERT

## 概要
このページでは、SimpleORM4Jを使用した登録処理（INSERT）に関してご説明をしていきます。  
SimpleORM4Jで実行できる登録処理は以下の2種類となります。

- Criteriaを使用した登録処理
- EntityManager#persistを使用した登録処理

## Criteriaを使用した登録処理
SimpleORM4Jでは、Criteriaで登録内容を作成してリレーショナルデータベースに登録することができます。  
処理の流れは以下となります。

<table>
    <tr>
        <th>処理1</th>
        <td>Criteria#insertで、登録テーブルを指定する。</td>
    </tr>
    <tr>
        <th>処理2</th>
        <td>Criteria#valueで、登録する値（Expression式）を指定する。</td>
    </tr>
</table>

更新系（INSERT・UPDATE・DELETE）クエリの実行は、Query#executeUpdateメソッドを実行します。  
このメソッドは登録・更新・削除をしたレコード数を返却します。

```
■ 実装例
try {
	EntityManager em = EntityManagerFactory.createEntityManager();
	Criteria criteria = em.getCriteria();
	criteria.insert(Charge.class)
		  .value(
			Expression.equal(Charge.class.getDeclaredField("name"), "担当者D"),
			Expression.equal(Charge.class.getDeclaredField("deleteFlag"), "0"),
			Expression.equal(Charge.class.getDeclaredField("registDate"), ":registDate"),
			Expression.equal(Charge.class.getDeclaredField("updateDate"), ":updateDate")
		  );
	Query query = em.createQuery(criteria);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
	query.setParameter("registDate", sdf.parse("2020-04-08 10:10:10"));
	query.setParameter("updateDate", sdf.parse("2020-04-08 10:10:10"));
	int insCnt = query.executeUpdate();
	System.out.println("Insert count : " + insCnt);
} catch (Exception e) {
	e.printStackTrace();
}
```

## EntityManager#persistを使用した登録処理
SimpleORM4Jでは、エンティティオブジェクトをリレーショナルデータベースに登録することができます。  
この場合は、EntityManager#persistメソッドを使用します。

```
■ 実装例
try {
	EntityManager em = EntityManagerFactory.createEntityManager();
	User user = new User();
	user.setAccount("test006");
	user.setName("テストユーザ006"); 
	user.setPassword("test006");
	user.setAdminFlag("1");
	user.setDeleteFlag("0");
	user.setRegistDate(LocalDate.of(2020, 4, 8));
	user.setUpdateDate(LocalDateTime.of(2020, 4, 8, 17, 17, 17));
	int insCnt = em.persist(user);
	System.out.println("Insert count : " + insCnt);
} catch (Exception e) {
	e.printStackTrace();
}
```
