# PandoLiA

穴実を巡回してレスをDBに保存するライブラリだよ

## 説明
穴実のスレ一覧を定期的に巡回してスレッドをみつけたらそのスレッドの巡回を開始して、レスをDBに保存するよ
データベースはSQLite3で、ファイルはdata/database.dbに保存するよ  

## 使用方法

ライブラリとしてIDEなどでインポートしたりしてつかってね  

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
TesLP tlp = new TesLP(); //LogParserを実装したクラス

PandoLiA pandolia = new PandoLiA();
pandolia.setLogParser(tlp);
Thread t = new Thread(pandolia);
t.start(); 
```

LogParser Interface
```
public interface LogParser {
    void loadThreadList(ArrayList<Thread5ch> threadList); //スレッド一覧を取得したとき
    void addThread(Thread5ch th);                         //スレッドの巡回が開始したとき
    void removeThread(Thread5ch th);                      //スレッドの巡回が終了したとき
    void insertedPost(Post5ch post);                      //DBにPostが挿入されたとき
    void insertedThread(Thread5ch th);                    //DBにThreadが挿入されたとき
    void updatedThread(Thread5ch th);                     //DBでThreadがUpdateされたとき
    void printErr(String s);                              //エラーとか
}
```

## ダウンロード(.jar)

- [1.0.0](https://github.com/Anarchist-xxxx/PandoLiA/releases/download/1.0.0/PandoLiA-1.0.0.jar)
