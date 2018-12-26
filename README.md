# PandoLiA

穴実を巡回してレスをDBに保存するライブラリだよ

## 説明
穴実のスレ一覧を定期的に巡回してスレッドをみつけたらそのスレッドの巡回を開始するよ  
データベースはSQLite3で、ファイルはdata/database.dbにあるよ

## 使用方法
* PandoLiAクラスをインスタンス化してrun()することで実行できるよ
```
new PandoLiA().run();
```

* RunnableなのでThreadでうごかすこともできるよ
```
PandoLiA pandolia = new PandoLiA();
Thread t = new Thread(pandolia);
t.start(); 
```

* LogParserを実装したクラスをセットすることで自由にログを出力できるよ  
LogParserがセットされてない場合System.out.print()で出力するよ
```
TestClass tc = new TestClass(); //LogParserを実装したクラス

PandoLiA pandolia = new PandoLiA();
pandolia.setLogParser(tc);
Thread t = new Thread(pandolia);
t.start(); 
```

LogParser Interface
```
public interface LogParser {
    void print(String s);                        //ぜんぶ
    void println(String s);                      //
    void printErr(String s);                     //エラーとか
    void addThread(Thread5ch th);                //Threadの巡回が開始したとき
    void removeThread(Thread5ch th);             //Threadの巡回が終了したとき
}
```

## jarダウンロード
ここからダウンロードしてね