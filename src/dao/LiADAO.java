package dao;

import model.Post5ch;
import model.Thread5ch;
import app.LogParser;

import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;

public class LiADAO implements Closeable {

    private static final String DBNAME = "data/database.db";
    private Connection con;
    private LogParser logParser;

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

            st.execute(
                    "CREATE TABLE `threads` ( `key` TEXT, `title` TEXT, `end` INTEGER, `start_time` TEXT, `end_time` TEXT, PRIMARY KEY(`key`) )"
            );

            st.execute(
                    "CREATE TABLE `posts` ( `key` TEXT, `number` INTEGER, `name` TEXT, `mail` TEXT, `time` TEXT, `comment` TEXT, `uid` TEXT, PRIMARY KEY(`key`,`number`) )"
            );

        } catch (SQLException e) {
            printErr("テーブル生成エラー？");
            analyzeSQLException(e);
        }
    }

    public void fixColumn() {
        try {

            Statement st = con.createStatement();

            //threadsテーブルの行一覧を取得
            ResultSet rs = st.executeQuery("PRAGMA TABLE_INFO('threads');");

            ArrayList<String> names = new ArrayList<String>();

            while(rs.next()) {
                names.add(rs.getString("name"));
            }

            //threads.start_timeがなかったら生成
            if(names.indexOf("start_time") == -1) {
                st.execute("alter table threads add column start_time text");
            }

            //threads.end_timeがなかったら生成
            if(names.indexOf("end_time") == -1) {
                st.execute("alter table threads add column end_time text");
            }

        } catch(SQLException e) {
            e.printStackTrace();
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
                insertedPost(post);

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
                insertedThread(th);

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
                updateThread(th);
                return true;
            }

        } catch(SQLException e) {
            analyzeSQLException(e);
        }

        return false;
    }

    public synchronized boolean updateStartTime(Thread5ch th) {
        try {
            String sqlInsert = "update threads set start_time = ? where key = ?";
            PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
            pstInsert.setString(1, th.getStartTime());
            pstInsert.setString(2, th.getKey());

            if(pstInsert.executeUpdate() > 0) {
                updateThread(th);
                return true;
            }

        } catch(SQLException e) {
            analyzeSQLException(e);
        }

        return false;
    }

    public synchronized boolean updateEndTime(Thread5ch th) {
        try {
            String sqlInsert = "update threads set end_time = ? where key = ?";
            PreparedStatement pstInsert = con.prepareStatement(sqlInsert);
            pstInsert.setString(1, th.getEndTime());
            pstInsert.setString(2, th.getKey());

            if(pstInsert.executeUpdate() > 0) {
                updateThread(th);
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
            printErr(e);
        }
    }

    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            printErr(e);
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

    private void insertedPost(Post5ch post) {
        if(logParser != null) {
            logParser.insertedPost(post);
        } else {
            System.out.println("DBにPostを挿入したよ (" + post.getNumber() + ": " + post.getName() + " " + post.getMail() + " " + post.getTime() + " " + post.getUid() + " | " + post.getComment() + ")");
        }
    }

    private void insertedThread(Thread5ch th) {
        if(logParser != null) {
            logParser.insertedThread(th);
        } else {
            System.out.println("DBにThreadを挿入したよ (" + th.getKey() + ": " + th.getTitle() + " [" + th.getEnd() + "]");
        }
    }

    private void updateThread(Thread5ch th) {
        if(logParser != null) {
            logParser.updatedThread(th);
        } else {
            System.out.println("Threadのendを更新したよ (end = " + th.getEnd() + ")");
        }
    }

}
