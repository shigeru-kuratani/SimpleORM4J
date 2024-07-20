# SimpleORM4Jを始める

## 概要
このページでは、SimpleORM4Jの使用方法をご説明していきます。  
Visual Studio Codeで以下の簡単なコンソールアプリケーションを作成していきます。

## リポジトリのクローン
Githubの「SimpleORM4J」のリポジトリをクローンします。 ※事前にGtiをインストールしておいてください。

- [リポジトリ](https://github.com/shigeru-kuratani/SimpleORM4J)

## ローカル環境にインストール
ターミナルでクローンしたディレクトリに移動して、以下のコマンドを実行し、ローカル環境にSimpleORM4Jをインストールします。

```
mvn install
```
※事前にMavenをインストールしておいてください。

## Mavenプロジェクトの作成
Visual Studio Codeで以下のコマンドを実行して、Mavenのプロジェクトを作成します。

```
Create Java Project
```
※Javaプロジェクトを作成する際に、Mavenを選択してください。

## pom.xmlの修正
pom.xmlに以下の依存ライブラリを追加します。

```
<dependency>
    <groupId>net.skuratani.simpleorm4j</groupId>
    <artifactId>simpleorm4j</artifactId>
    <version>0.0.3</version>
</dependency>
```
※JDBCは接続するデータベースに合わせてライブラリを追加してください。

## 設定ファイル（so4j.xml）の追加
設定ファイル（so4j.xml）を「src/main/java」フォルダに追加します。

コンテナのコネクションプールを使用せずに直接JDBCを使用してデータベースに接続処理を実行する場合は、 設定ファイル（so4j.xml）に以下の情報を定義します。

- URL
- ユーザ
- パスワード

```
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
<comment>SimpleORM4J設定ファイル</comment>
<entry key="url">jdbc:mysql://localhost:3306/SimpleORM4J</entry>
<entry key="user">SimpleORM4J</entry>
<entry key="password">SimpleORM4J</entry>
</properties>
```

## ソースコードの作成
サンプルプログラムでは、学部テーブル（mst_department）から「学校ID=2」「学部名=文学部」のデータを検索して、コンソールに表示してみます。  
※ コンソール表示の際に、表形式で表示する為に「helper.EntityResultViewHelper.java」を使用しています。

```
■ entity.Department.java
package entity;

import java.util.Date;

import lombok.Data;

import net.skuratani.simpleorm4j.annotation.Column;
import net.skuratani.simpleorm4j.annotation.Entity;
import net.skuratani.simpleorm4j.annotation.GenerateValue;
import net.skuratani.simpleorm4j.annotation.Id;
import net.skuratani.simpleorm4j.annotation.Table;
import net.skuratani.simpleorm4j.type.GenerationType;

/**
 * 学部エンティティ
 */
@Entity
@Table(name="mst_department")
@Data
public class Department {

	@Id
	@GenerateValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private int id;

	@Column(name="name")
	private String name;

	@Column(name="school_id")
	private String schoolId;

	@Column(name="delete_flag")
	private String deleteFlag;

	@Column(name="regist_date")
	private Date registDate;

	@Column(name="update_date")
	private Date updateDate;
}
```

```
■実行ソース
package exec;

import java.util.List;

import entity.Department;
import helper.EntityResultViewHelper;
import net.skuratani.simpleorm4j.expression.Expression;
import net.skuratani.simpleorm4j.persistence.Criteria;
import net.skuratani.simpleorm4j.persistence.EntityManager;
import net.skuratani.simpleorm4j.persistence.EntityManagerFactory;
import net.skuratani.simpleorm4j.persistence.Query;

/**
 * SimpleORM4Jコンソールアプリケーションサンプル
 */
public class Sample {

	/**
	 * main.
	 * @param args
	 */
	public static void main(String[] args) {
		new Sample().execute();
	}

	/**
	 * execute.
	 */
	private void execute() {
		try {
			EntityManager em = EntityManagerFactory.createEntityManager();
			Criteria criteria = em.getCriteria();
			criteria.select(Department.class)
				  .from(Department.class)
				  .where(Expression.equal(Department.class.getDeclaredField("schoolId"), 2),
					     Expression.equal(Department.class.getDeclaredField("name"), "文学部"));
			Query query = em.createQuery(criteria);
			List<Department> resultList = (List<Department>) query.getResultList();
			System.out.println(EntityResultViewHelper.generateTableView(resultList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
```

## サンプル実行結果
サンプルプログラムの実行結果は以下のようになります。

```
■ 実行結果
id   name     schoolId   deleteFlag   registDate   　updateDate              
---------------------------------------------------------------------------
7    文学部   2          　0　　            2017-01-07   2017-01-07 10:10:10.0
```
