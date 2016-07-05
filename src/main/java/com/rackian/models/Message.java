package com.rackian.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable, Comparable<Message> {

    public static final int STATUS_SENT = 1;
    public static final int STATUS_RECEIVED = 2;
    public static final int STATUS_READ = 3;

    private User userOri;
    private User userDest;
    private String message;
    private LocalDateTime time;
    private int status;

    public Message() {
    }

    public Message(User userOri, User userDest, String message, LocalDateTime time) {
        this.userOri = userOri;
        this.userDest = userDest;
        this.message = message;
        this.time = time;
    }

    public User getUserOri() {
        return userOri;
    }

    public void setUserOri(User userOri) {
        this.userOri = userOri;
    }

    public User getUserDest() {
        return userDest;
    }

    public void setUserDest(User userDest) {
        this.userDest = userDest;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getTimeString() {
        String time;
        time = "";

        if(this.time.getHour() < 10) {
            time += "0";
        }
        time += this.time.getHour();

        time += ":";

        if (this.time.getMinute() < 10) {
            time += "0";
        }
        time += this.time.getMinute();

        return time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int compareTo(Message o) {
        return this.getTime().compareTo(o.getTime());
    }

}
