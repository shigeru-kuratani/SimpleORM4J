# SimpleORM4J

## What's SimpleORM4J?

SimpleORM4Jは、Javaアプリケーションの為のシンプルなO/Rマッパです。
アプリケーションに対して、シンプルなCriteria APIを提供し、簡易な処理でリレーショナルデータベースのデータを Javaクラスであるエンティティのオブジェクトして直感的に操作することができます。
JPA（Java Persistence API、Jakarta Persistence）が提供するCriteria APIを簡素化したインターフェイスを提供して、簡潔なデータ操作を実現します。

JPAのように永続化コンテキストを使用した高度な処理は実装されていません。
しかし、SQLを記述せずに直接Javaプログラム上でクエリを生成・管理し、簡潔かつ直感的な処理でデータベースへの問合せを実現したい場合には、SimpleORM4Jは良い道具になると思います。

SimpleORM4Jですが、Criteria APIで定義したクエリは標準SQLとしてリレーショナルデータベースに問い合わせが実行されます。
Oracle Database・MySQL・PostgreSQLで動作確認をしておりますが、その他のデータベースに関しては各位で動作確認をお願い致します。

SimpleORM4Jは、GPLv3([http://www.gnu.org/licenses/gpl.html](http://www.gnu.org/licenses/gpl.html))に基づいて配布を致します。
