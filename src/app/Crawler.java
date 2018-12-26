package app;

import dao.LiADAO;
import main.com.j5.connect.J5ch;
import main.com.j5.connect.ResultSet;
import main.com.j5.exception.ChinkoException;
import main.com.j5.exception.UnkoException;
import model.Post5ch;
import model.Thread5ch;

public class Crawler implements Runnable {
    private Thread5ch th;
    private LiADAO dao;
    private Logger logger;

    private int num;

    public void run() {

        println("巡回を開始するよ (" + th.getKey() + ": " + th.getTitle() + ")");

        num = 1;

        String app = "JYW2J6wh9z8p8xjGFxO3M2JppGCyjQ";
        String sec = "hO2QHdapzbqbTFOaJgZTKXgT2gWqYS";
        String auth_x_2ch_ua  = "JaneStyle/4.0.0";
        String useragent = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)";

        String host = "agree";
        String bbs = "liveanarchy";

        J5ch j5 = new J5ch(host, bbs);

        insertThread();

        try {
            j5.auth5chAPI(app, sec, useragent, auth_x_2ch_ua);
            j5.setUserAgent(useragent);

            ResultSet rs = j5.get(th.getKey());

            insertPost(rs.toStringBody().split("\n"));

            int size = rs.bytes;

            while(true) {
                Thread.sleep(1000 * 60 * 2);

                rs = j5.get(th.getKey(), rs.lastmodify, size);
                size += rs.bytes;

                insertPost(rs.toStringBody().split("\n"));
            }

        } catch(UnkoException e) {
            //;;;
        } catch(ChinkoException e) {
            println("Unknown error on Thread key: " + th.getKey());
        } catch(InterruptedException e) {
            println("Unknown error on key: " + th.getKey() + " Thread");
        }

        insertEnd();

        print("Threadの巡回が終了したよ");
        println(" (key: " + th.getKey() + " title: " + th.getTitle() + ")");

    }

    public void setTh(Thread5ch th) {
        this.th = th;
    }

    public void setLiADAO(LiADAO dao) {
        this.dao = dao;
    }

    private void insertThread() {
        dao.insertThread(th);
    }

    private void insertPost(String[] rawData) {
        for(String row: rawData) {
            String[] splited = row.split("<>");

            Post5ch post = new Post5ch();
            post.setNumber(num);
            post.setName(splited[0]);
            post.setMail(splited[1]);

            if(splited[2].lastIndexOf(" ID:") < 0) {
                post.setTime(splited[2].trim());
                post.setUid("");
            } else {
                String[] timeAndID = splited[2].split(" ID:");
                post.setTime(timeAndID[0]);
                post.setUid(timeAndID[1]);
            }

            post.setComment(splited[3]);

            dao.insertPost(post, th);

            num++;
        }
    }

    private void insertEnd() {
        dao.insertEnd(th);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    private void print(String s) {
        if(logger != null) {
            logger.print(s);
        } else {
            System.out.print(s);
        }
    }

    private void println(String s) {
        if(logger != null) {
            logger.println(s);
        } else {
            System.out.println(s);
        }
    }
}
