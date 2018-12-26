package app;

import model.Post5ch;
import model.Thread5ch;

public interface LogParser {
    void print(String s);
    void println(String s);
    void printErr(String s);
    void addThread(Thread5ch th);
    void removeThread(Thread5ch th);
}
