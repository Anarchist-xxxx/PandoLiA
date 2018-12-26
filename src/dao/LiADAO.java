package dao;

import model.Post5ch;
import model.Thread5ch;
import app.LogParser;
import org.sqlite.SQLiteException;

import java.io.Closeable;
import java.sql.*;

public class LiADAO implements Closeable {

    public static final String DBNAME = "data/database.db";
    private Connection con;
    private LogParser logParser;

    public static final int ORDER_THREAD = 1;
    public static final int ORDER_POST = 2;
    public static final int ORDER_END = 3;

    public LiADAO() {
        con = getConnection();
    }

    public void setlogger(LogParser logParser) {
        this.logParser = logParser;
    }

    private Connection getConnection() {
        Connection result = null;

        try {
            Class.forName("org.sqlite.JDBC");
            result = DriverManager.getConnection("jdbc:sqlite:" + DBNAME);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found");
        }

        return result;
    }

    public void createTable() {
        try {
            Statement st = con.createStatement();

            st.execute("CREATE TABLE `posts` ( `key` TEXT, `number` INTEGER, `name` TEXT, `mail` TEXT, `time` TEXT, `comment` TEXT, `uid` TEXT, PRIMARY KEY(`key`,`number`) )");
            st.execute("CREATE TABLE `threads` ( `key` TEXT, `title` TEXT, `end` INTEGER, PRIMARY KEY(`key`) )");

        } catch (SQLException e) {
            logParser.println("テーブル生成エラー？");
            analyzeSQLException(e);
        }
    }

    public synchronized boolean insertPost(Post5ch post, Thread5ch th) {

        String sql = "INSERT INTO posts(key, number, name, mail, time, comment, uid) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, th.getKey());
            pst.setInt(2, post.getNumber());
            pst.setString(3, post.getName());
            pst.setString(4, post.getMail());
            pst.setString(5, post.getTime());
            pst.setString(6, post.getComment());
            pst.setString(7, post.getUid());


            if(pst.executeUpdate() > 0) {
                logParser.print("DBにPostを挿入したよ");
                logParser.print(" (" + post.getNumber() + ": " + post.getName() + " " + post.getMail() + " " + post.getTime() + " " + post.getUid() + " | ");
                logParser.println(post.getComment() + ")");

                return true;
            }

        } catch(SQLException e) {
            analyzeSQLException(e);
        }

        return false;
    }

    public synchronized boolean insertThread(Thread5ch th) {

        String sql = "INSERT INTO threads(key, title, end) VALUES(?, ?, -1)";

        try {
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, th.getKey());
            pst.setString(2, th.getTitle());

            if(pst.executeUpdate() > 0) {
                logParser.print("DBにThreadを挿入したよ");
                logParser.println(" (" + th.getKey() + ": " + th.getTitle() + "(-1)");

                return true;
            }

        } catch(SQLException e) {
            analyzeSQLException(e);
        }
        return false;
    }

    public synchronized int getThreadEnd(Thread5ch th) {
        try {
            String sql = "select count(*) as end from posts where key = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, th.getKey());

            ResultSet rs = pst.executeQuery();

            return rs.getInt("end");

        } catch(SQLException e) {
            analyzeSQLException(e);
        }

        return -1;
    }

    public synchronized boolean insertEnd(Thread5ch th) {
        try {
            String sqlInsert = "update threads set end = ? where key = ?";
            PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
            pstInsert.setInt(1, th.getEnd());
            pstInsert.setString(2, th.getKey());

            if(pstInsert.executeUpdate() > 0) {
                logParser.println("Threadのendを更新したよ (end = " + th.getEnd() + ")");
                return true;
            }

        } catch(SQLException e) {
            analyzeSQLException(e);
        }

        return false;
    }

    private void analyzeSQLException(SQLException e) {
        if(e.getMessage().startsWith("[SQLITE_CONSTRAINT_PRIMARYKEY]")) {
            printErr("[SQLITE_CONSTRAINT_PRIMARYKEY] 主キー重複");
        } else if(e.getMessage().startsWith("[SQLITE_BUSY]")) {
            printErr("[SQLITE_BUSY] DBへの多重アクセス");
        } else {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            System.err.println("err");
        }
    }

    private void println(String s) {
        if(logParser != null) {
            logParser.println(s);
        } else {
            System.out.println(s);
        }
    }

    private void print(String s) {
        if(logParser != null) {
            logParser.print(s);
        } else {
            System.out.print(s);
        }
    }

    private void printErr(String s) {
        if(logParser != null) {
            logParser.printErr(s);
        } else {
            System.out.println(s);
        }
    }

}
