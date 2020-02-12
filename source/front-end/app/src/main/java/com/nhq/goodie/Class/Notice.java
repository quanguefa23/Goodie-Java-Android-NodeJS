package com.nhq.goodie.Class;

public class Notice {
    String id;
    int type;
    String from;
    boolean mark;

    public Notice(String id, int type, String from, boolean mark) {
        this.id = id;
        this.type = type;
        this.from = from;
        this.mark = mark;
    }

    public boolean getMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Notice getDuplicate() {
        return new Notice(id, type, from, mark);
    }
}
