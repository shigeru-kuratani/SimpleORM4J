# DELETE

## 概要
このページでは、SimpleORM4Jを使用した削除処理（DELETE）に関してご説明をしていきます。  
SimpleORM4Jで実行できる削除処理は以下の2種類となります。

- Criteriaを使用した削除処理
- EntityManager#removeを使用した削除処理

## Criteriaを使用した削除処理
SimpleORM4Jでは、Criteriaで削除条件を作成してリレーショナルデータベースのデータを削除することができます。  
処理の流れは以下となります。

<table>
    <tr>
        <th>処理1</th>
        <td>Criteria#deleteで、削除テーブルを指定する。</td>
    </tr>
    <tr>
        <th>処理2</th>
        <td>Criteria#whereで、削除レコード条件を指定する。</td>
    </tr>
</table>

更新系（INSERT・UPDATE・DELETE）クエリの実行は、Query#executeUpdateメソッドを実行します。  
このメソッドは登録・更新・削除をしたレコード数を返却します。

```
■ 実装例
try {
	EntityManager em = EntityManagerFactory.createEntityManager();
	Criteria criteria = em.getCriteria();
	criteria.delete(Charge.class)
		  .where(Expression.equal(Charge.class.getDeclaredField("id"), 6));
	Query query = em.createQuery(criteria);
	int delCnt = query.executeUpdate();
	System.out.println("Delete count : " + delCnt);
} catch (Exception e) {
	e.printStackTrace();
}
```

## EntityManager#removeを使用した削除処理
SimpleORM4Jでは、エンティティオブジェクトを削除して、リレーショナルデータベースに反映することができます。  
この場合は、EntityManager#removeメソッドを使用します。  
EntityManager#removeメソッドでは、削除対象のオブジェクトの@Idアノテーションが記述されたフィールド値で一意にオブジェクトを特定して、削除処理を実行します。

```
■ 実装例
try {
	EntityManager em = EntityManagerFactory.createEntityManager();
	Criteria criteria = em.getCriteria();
	criteria.select(Charge.class)
		  .from(Charge.class)
		  .where(Expression.equal(Charge.class.getDeclaredField("id"), 7));
	Query query = em.createQuery(criteria);
	Charge charge = (Charge) query.getSingleResult();

	int delCnt = em.remove(charge);
	System.out.println("Delete count : " + delCnt);
} catch (Exception e) {
	e.printStackTrace();
}
```
