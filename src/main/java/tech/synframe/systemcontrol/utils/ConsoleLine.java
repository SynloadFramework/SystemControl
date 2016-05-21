package tech.synframe.systemcontrol.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by Nathaniel on 5/21/2016.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class ConsoleLine {
    public String line;
    public int id;
    public ConsoleLine(String line, int id){
        this.line = line;
        this.id = id;
    }
    public String getLine() {
        return line;
    }
    public void setLine(String line) {
        this.line = line;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
