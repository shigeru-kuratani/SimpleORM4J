# SimpleORM4J Change Log

## ver 0.0.1
初版作成

## ver 0.0.2
1. Query#getSingleResultの実行で検索対象が存在しない場合に、例外が発生するバグを修正
2. SELECT処理で複数フィールドを指定した場合に、例外が発生するバグを修正（QueryBuilder#judgeEntityClass）

## ver 0.0.3
1. SQLiteデータベースへの接続に対応<br>
（設定ファイルにURLのみ記述する）
2. 冗長モード（verbose）に対応<br>
設定ファイルの冗長モード（verbose）をtrueに設定すると、データベースアクセスの情報が標準出力に表示
