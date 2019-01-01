package app;

import model.Post5ch;
import model.Thread5ch;

import java.util.ArrayList;

public interface LogParser {
    void loadThreadList(ArrayList<Thread5ch> threadList);
    void addThread(Thread5ch th);
    void removeThread(Thread5ch th);
    void insertedPost(Post5ch post);
    void insertedThread(Thread5ch th);
    void updatedThread(Thread5ch th);
    void printErr(String s);
    void printErr(Exception e);
}
