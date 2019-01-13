package app;

import dao.LiADAO;
import main.com.j5.connect.J5ch;
import main.com.j5.connect.ResultSet;
import main.com.j5.connect.method.SuperAnalizer;
import main.com.j5.exception.UnkoException;
import model.Thread5ch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class PandoLiA implements Runnable {

    LogParser logParser;

    public void setLogParser(LogParser logParser) {
        this.logParser = logParser;
    }

    public void run() {

        //ディレクトリとかはちゃんとあるかな
        checkFiles();

        //DAOつくって
        LiADAO dao = new LiADAO();
        dao.setlogger(logParser);

        //J5chの用意
        J5ch j5 = new J5ch("agree", "liveanarchy");
        j5.setAnal(new SuperAnalizer());

        //スレッド生存確認用ハッシュマップ
        HashMap<String, Boolean> hm = new HashMap<String, Boolean>();

        while(true) {
            try {
                //make Thread5ch List
                ArrayList<Thread5ch> threadList = new ArrayList<Thread5ch>();
                ResultSet rs = j5.getSubject();

                String[] raw = rs.toStringBody().split("\n");
                for(String tmp: raw) {
                    String[] splited = tmp.split(".dat<>");
                    String key = splited[0];
                    String title = splited[1].substring(0, splited[1].lastIndexOf("(") - 2);

                    if(!key.startsWith("924")) {
                        threadList.add(new Thread5ch(key, title));
                    }
                }

                loadThreadList(threadList);

                //スレ一覧を全部まわす
                for(Thread5ch th: threadList) {

                    //そのスレッドのHashMapがない(=クロールが開始してないとき)
                    if(hm.get(th.getKey()) == null) {
                        hm.put(th.getKey(), true);

                        //クローラーの準備
                        Crawler c = new Crawler();
                        c.setTh(th);
                        c.setLiADAO(dao);
                        c.setLogParser(logParser);

                        //Threadにして実行！
                        Thread thread = new Thread(c);
                        thread.setName(th.getKey() + ": " + th.getTitle());
                        thread.start();
                    }
                }

                //拡張for文の中でremoveするとこわれるからremove対象をつんでおくキューを用意するよ
                ArrayList<String> removeList = new ArrayList<String>();

                //hmのkeyを全部まわすよ
                for(String key: hm.keySet()) {
                    boolean isAlive = false;

                    //スレ一覧とてらしあわせてスレの生存チェックするよ
                    for(Thread5ch th: threadList) {
                        if(th.getKey().equals(key)) {
                            isAlive = true;
                            break;
                        }
                    }

                    //スレがおちてたらremoveリストに追加！
                    if(!isAlive) {
                        removeList.add(key);
                    }
                }

                //おちたスレをクロール中スレ一覧からリムーブ！
                for(String key: removeList) {
                    hm.remove(key);
                }

                //おねんね
                Thread.sleep(1000 * 40);

            } catch (InterruptedException e) {
                printErr(e);
            } catch (UnkoException e) {
                printErr(e);
            }
        }
    }

    private void printErr(String s) {
        if(logParser != null) {
            logParser.printErr(s);
        } else {
            System.out.println(s);
        }
    }

    private void printErr(Exception e) {
        if(logParser != null) {
            logParser.printErr(e);
        } else {
            e.printStackTrace();
        }
    }

    private void loadThreadList(ArrayList<Thread5ch> threadList) {
        if(logParser != null) {
            logParser.loadThreadList(threadList);
        } else {
            System.out.println("スレッド一覧を取得したよ ('" + threadList.size() + "'スレッド)");
        }
    }

    public void checkFiles() {
        File dataDir = new File("data/");

        if(!dataDir.exists()) {
            dataDir.mkdir();
        }

        File db = new File("./data/database.db");

        if(!db.exists()) {
            LiADAO tmp = new LiADAO();

            tmp.createTable();

            tmp.close();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //テーブルのカラムがたりなかったら追加するよ
        LiADAO dao = new LiADAO();
        dao.fixColumn();
        dao.close();

    }
}
