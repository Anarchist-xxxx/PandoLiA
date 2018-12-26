package app;

import dao.LiADAO;
import main.com.j5.connect.J5ch;
import main.com.j5.connect.ResultSet;
import main.com.j5.exception.ChinkoException;
import main.com.j5.exception.UnkoException;
import model.Post5ch;
import model.Thread5ch;

public class Crawler implements Runnable {
    private static final String APP = "JYW2J6wh9z8p8xjGFxO3M2JppGCyjQ";
    private static final String SEC = "hO2QHdapzbqbTFOaJgZTKXgT2gWqYS";
    private static final String AUTH_X_2CH_UA  = "JaneStyle/4.0.0";
    private static final String USERAGENT = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)";

    private static final String HOST = "agree";
    private static final String BBS = "liveanarchy";

    private Thread5ch th;
    private LiADAO dao;
    private LogParser logParser;

    private int num;

    public void run() {

        if(logParser != null) {
            logParser.addThread(th);
        } else {
            System.out.println("Threadの巡回を開始するよ (" + th.getKey() + ": " + th.getTitle() + ")");
        }

        //number
        num = 1;

        J5ch j5 = new J5ch(HOST, BBS);

        insertThread();

        try {
            j5.auth5chAPI(APP, SEC, USERAGENT, AUTH_X_2CH_UA);
            j5.setUserAgent(USERAGENT);

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
            printErr("おちんちんエラー on Thread key: " + th.getKey());
        } catch(InterruptedException e) {
            printErr("InterruptedException on key: " + th.getKey() + " Thread");
        }

        insertEnd();

        if(logParser != null) {
            logParser.removeThread(th);
        } else {
            System.out.println("Threadの巡回が終了したよ (" + th.getKey() + ": " + th.getTitle() + ")");
        }

    }

    private void printErr(String s) {
        if(logParser != null) {
            logParser.printErr(s);
        } else {
            System.out.println(s);
        }
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

    public void setLogParser(LogParser logParser) {
        this.logParser = logParser;
    }




}
