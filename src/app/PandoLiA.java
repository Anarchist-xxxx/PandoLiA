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

    Logger logger;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void run() {
        println("run");

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

        LiADAO LiADAO = new LiADAO();

        J5ch j5 = new J5ch("agree", "liveanarchy");
        j5.setAnal(new SuperAnalizer());

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

                println("スレッド一覧を取得したよ ('" + threadList.size() + "'スレッド)");

                for(Thread5ch th: threadList) {
                    if(hm.get(th.getKey()) == null) {
                        hm.put(th.getKey(), true);
                        Crawler c = new Crawler();
                        c.setTh(th);
                        c.setLiADAO(LiADAO);
                        Thread thread = new Thread(c);
                        thread.setName(th.getKey() + ": " + th.getTitle());
                        thread.start();
                    }
                }

                ArrayList<String> removeList = new ArrayList<String>();

                for(String key: hm.keySet()) {
                    boolean isAlive = false;
                    for(Thread5ch th: threadList) {
                        if(th.getKey().equals(key)) {
                            isAlive = true;
                            break;
                        }
                    }
                    if(!isAlive) {
                        removeList.add(key);
                    }
                }

                for(String key: removeList) {
                    hm.remove(key);
                }

                Thread.sleep(1000 * 40);

            } catch (InterruptedException e) {
                println("inter");
            } catch (UnkoException e) {
                println("unti");
            }
        }
    }

    private void println(String s) {
        if(logger != null) {
            logger.println(s);
        } else {
            System.out.println(s);
        }
    }

    private void print(String s) {
        if(logger != null) {
            logger.print(s);
        } else {
            System.out.print(s);
        }
    }
}
