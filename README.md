# SimpleORM4J

## What's SimpleORM4J?

SimpleORM4Jは、Javaアプリケーションの為のシンプルなO/Rマッパです。
アプリケーションに対して、シンプルなCriteria APIを提供し、簡易な処理でリレーショナルデータベースのデータを Javaクラスであるエンティティのオブジェクトして直感的に操作することができます。
JPA（Java Persistence API、Jakarta Persistence）が提供するCriteria APIを簡素化したインターフェイスを提供して、簡潔なデータ操作を実現します。

JPAのように永続化コンテキストを使用した高度な処理は実装されていません。
しかし、SQLを記述せずに直接Javaプログラム上でクエリを生成・管理し、簡潔かつ直感的な処理でデータベースへの問合せを実現したい場合には、SimpleORM4Jは良い道具になると思います。

SimpleORM4Jですが、Criteria APIで定義したクエリは標準SQLとしてリレーショナルデータベースに問い合わせが実行されます。  
Oracle Database・SQL Server・MySQL・PostgreSQLで動作確認をしております。

## ドキュメント

- [SimpleORM4Jについて](./doc/SimpleORM4Jについて.md)
- [SimpleORM4Jを始める](./doc/SimpleORM4Jを始める.md)
- [設定ファイル](./doc/設定ファイル.md)
- [エンティティ](./doc/エンティティ.md)
- [SELECT](./doc/SELECT.md)
- [INSERT](./doc/INSERT.md)
- [UPDATE](./doc/UPDATE.md)
- [DELETE](./doc/DELETE.md)
- [式表現](./doc/式表現.md)
- [名前付プレースホルダ](./doc/名前付プレースホルダ.md)
- [RAWクエリ](./doc/RAWクエリ.md)
- [トランザクション](./doc/トランザクション.md)
- [例外](./doc/例外.md)

## ライセンス

SimpleORM4Jは、GPLv2 with the Classpath Exceptionに基づいて配布を致します。  
[GNU GPL v2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.html)

## 変更履歴

- [changelog](./changelog.md)
