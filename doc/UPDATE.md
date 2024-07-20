# UPDATE

## 概要
このページでは、SimpleORM4Jを使用した更新処理（UPDATE）に関してご説明をしていきます。
SimpleORM4Jで実行できる更新処理は以下の2種類となります。

- Criteriaを使用した更新処理
- EntityManager#mergeを使用した更新処

## Criteriaを使用した更新処理
SimpleORM4Jでは、Criteriaで更新内容を作成してリレーショナルデータベースのデータを更新することができます。  
処理の流れは以下となります。

<table>
    <tr>
        <th>処理1</th>
        <td>Criteria#updateで、更新テーブルを指定する。</td>
    </tr>
    <tr>
        <th>処理2</th>
        <td>Criteria#setで、更新する値（Expression式）を指定する</td>
    </tr>
    <tr>
        <th>処理3</th>
        <td>Criteria#whereで、更新レコード条件を指定する</td>
    </tr>
</table>

更新系（INSERT・UPDATE・DELETE）クエリの実行は、Query#executeUpdateメソッドを実行します。  
このメソッドは登録・更新・削除をしたレコード数を返却します。

```
■ 実装例
try {
	EntityManager em = EntityManagerFactory.createEntityManager();
	Criteria criteria = em.getCriteria();
	criteria.update(Charge.class)
		  .set(Expression.equal(Charge.class.getDeclaredField("name"), "担当者D更新"),
			 Expression.equal(Charge.class.getDeclaredField("registDate"), ":registDate"),
			 Expression.equal(Charge.class.getDeclaredField("updateDate"), ":updateDate"))
		  .where(Expression.equal(Charge.class.getDeclaredField("id"), 6));
	Query query = em.createQuery(criteria);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
	query.setParameter("registDate", sdf.parse("2020-04-08 20:20:20"));
	query.setParameter("updateDate", sdf.parse("2020-04-08 20:20:20"));
	int updCnt = query.executeUpdate();
	System.out.println("Update count : " + updCnt);
} catch (Exception e) {
	e.printStackTrace();
}
```

## EntityManager#mergeを使用した更新処
SimpleORM4Jでは、エンティティオブジェクトを更新して、リレーショナルデータベースに登録することができます。  
この場合は、EntityManager#mergeメソッドを使用します。  
EntityManager#mergeメソッドでは、更新対象のオブジェクトの@Idアノテーションが記述されたフィールド値で一意にオブジェクトを特定して、更新処理を実行します。

```
■ 実装例
try {
	EntityManager em = EntityManagerFactory.createEntityManager();
	Criteria criteria = em.getCriteria();
	criteria.select(User.class)
		  .from(User.class)
		  .where(Expression.equal(User.class.getDeclaredField("id"), ":id"));
	Query query = em.createQuery(criteria);
	query.setParameter("id", 5);
	User user = (User) query.getSingleResult();

	user.setName("テストユーザ006");
	user.setRegistDate(LocalDate.of(2020, 4, 8));
	user.setUpdateDate(LocalDateTime.of(2020, 4, 8, 18, 18, 18));
	int updCnt = em.merge(user);
	System.out.println("Update count : " + updCnt);
} catch (Exception e) {
	e.printStackTrace();
}
```
