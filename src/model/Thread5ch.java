package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class Thread5ch {
    private String key;
    private String title;
    private int end = -1;
    private String startTime;
    private String endTime;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public StringProperty startTimeProperty() {
        return new SimpleStringProperty(startTime);
    }

    public StringProperty endTimeProperty() {
        return new SimpleStringProperty(endTime);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Thread5ch) {
            Thread5ch target = (Thread5ch)obj;

            return target.getKey().equals(this.key);
        }
        return false;
    }
}
