package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class Thread5ch {
    private String key;
    private String title;
    private int end;

    public Thread5ch() {
    }

    public Thread5ch(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public StringProperty keyProperty() {
        return new SimpleStringProperty(key);
    }

    public StringProperty titleProperty() {
        return new SimpleStringProperty(title);
    }

    public IntegerProperty endProperty() {
        return new SimpleIntegerProperty(end);
    }



}
