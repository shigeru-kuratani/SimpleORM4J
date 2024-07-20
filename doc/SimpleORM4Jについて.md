# SimpleORM4Jについて

## 開発の動機
システム開発の仕事をさせていただく中で、いくつかのシステムの新規開発やリプレースを経験させていただきました。  
その中には、[Hibernate](https://hibernate.org/)や[MyBatis](https://blog.mybatis.org/)などのオープンソースORMを使用したものありましたし、 システム独自にORMを実装しているものもありました。  

仕事と並行してJPA（Java Persistence API、Jakarta Persistence）の勉強をしていて、 「高度な処理や柔軟な処理はできなくても、出来る限り簡潔で分かりやすい処理でORMを実現するOSSがあっても良いのでは」と思ったのが開発の動機です。  
私の不勉強で知らないだけで、著名なORM以外にも素晴らしいコンセプトのORMがたくさん存在するのだと思います。その中の一つにSimpleORM4Jが仲間入りできれば良いと思っています。

SimpleORM4Jがフィットするアプリケーションですが、SQLを記述してシステム内で管理せずに、Javaプログラム上で データベースへのクエリを生成・管理する方が効率が良い、比較的小さなアプリケーションになると思います。  
また、JPAではParameterExpression等を使用して複雑なクエリも生成できますが、SimpleORM4Jではクエリ生成処理を簡素化する為にあまり複雑なクエリは生成できません。ですので、比較的シンプルなクエリのみ発行するアプリケーション向けになると思います。  
SimpleORM4Jでは、データベース製品固有の機能や、複雑なSQLを発行する為に生SQLを発行するインターフェイスを提供しています。

## 簡単な実装例
SimpleORM4Jを使用した簡単な実装例を以下に示します。  
SimpleORM4Jを使用すれば、リレーショナルデータベースのデータを、Javaクラスであるエンティティとして簡潔かつ直感的に操作することができます。

```
■ Entityクラス
package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

import net.skuratani.simpleorm4j.annotation.Column;
import net.skuratani.simpleorm4j.annotation.Entity;
import net.skuratani.simpleorm4j.annotation.GenerateValue;
import net.skuratani.simpleorm4j.annotation.Id;
import net.skuratani.simpleorm4j.annotation.Table;
import net.skuratani.simpleorm4j.type.GenerationType;

/**
 * ユーザマスタエンティティ
 */
@Entity
@Table(name="MST_USER")
@Data
public class User {

	@Id
	@GenerateValue(strategy=GenerationType.SEQUENCE, sequence="SEQ_MST_USER")
	@Column(name="ID")
	private int id;

	@Column(name="NAME")
	private String name;

	@Column(name="REGIST_DATE")
	private LocalDate registDate;

	@Column(name="UPDATE_DATE")
	private LocalDateTime updateDate;
}
```

```
■ 簡単な実装例
※ EntityResultViewHelperは検索データをテーブル表示する為に使用しています
import java.util.List;

import entity.User;
import helper.EntityResultViewHelper;
import net.skuratani.simpleorm4j.persistence.Criteria;
import net.skuratani.simpleorm4j.persistence.EntityManager;
import net.skuratani.simpleorm4j.persistence.EntityManagerFactory;
import net.skuratani.simpleorm4j.persistence.Query;

public class SimpleExample {

	/**
	 * main.
	 * @param args
	 */
	public static void main(String args) {
		new SimpleExample().execute();
	}

	/**
	 * execute.
	 */
	private void execute() {
		try {
			EntityManager em = EntityManagerFactory.createEntityManager();
			Criteria criteria = em.getCriteria();
			criteria.select(User.class)
				  .from(User.class);
			Query query = em.createQuery(criteria);
			List<User> resultList = (List<User>) query.getResultList();
			System.out.println(EntityResultViewHelper.generateTableView(resultList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```

```
■ 実行結果
id   name　　　　　　registDate　　updateDate            
----------------------------------------------------------
1    テストユーザ001   2020-04-20   2020-04-20T00:00      
2    テストユーザ002   2020-04-20   2020-04-20T00:00      
3    テストユーザ003   2020-04-20   2020-04-20T00:00  
```

UserマスタテーブルからUserエンティティのデータをSELECTすることが直感的にわかると思います。  
Javaクラスとリレーショナルデータベーステーブル・カラムの関連付けはアノーテーションで定義しておきますので、 Javaクラスのオブジェクトとしてデータの検索が実行できます。

## ライセンス
本プログラムは、GPLv2 with the Classpath Exceptionとして配布を致します。  
[GNU GPL v2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.html)  
本プログラムの利用者は、独立したモジュールと本ライブラリをリンクして実行可能プログラムを生成し、利用者が選んだ条件の元で結果の実行可能プログラムを複製および配布することができます。 独立したモジュールとは、本ライブラリの派生物でもなく、本ライブラリを基にしてもいないモジュールです。

また、著作権者、または上記で 許可されている通りに『プログラム』を改変または再頒布したその他の団体は、 あなたに対して『プログラム』の利用ないし利用不能で生じた通常損害や特別 損害、偶発損害、 間接損害(データの消失や不正確な処理、あなたか第三者が 被った損失、あるいは『プログラム』が他のソフトウェアと一緒に動作しない という不具合などを含むがそれらに限らない)に一切の責任を負いません。  
本プログラムは利用者の責任において使用されますようにお願い致します。
