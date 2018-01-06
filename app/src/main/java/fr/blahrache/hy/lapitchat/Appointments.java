package fr.blahrache.hy.lapitchat;

import java.io.Serializable;

/**
 * Created by HP on 03/01/2018.
 */

public class Appointments implements Serializable {

    private String subject;
    private String body;
    private String address;
    private int year;
    private int mounth;
    private int day;
    private int hour;
    private int minute;
    private String type;
    private String seen = "no";


    public Appointments() {
        super();
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getMounth() {
        return mounth;
    }
    public void setMounth(int mounth) {
        this.mounth = mounth;
    }
    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getSeen() {
        return seen;
    }
    public void setSeen(String seen) {
        this.seen = seen;
    }

    @Override
    public String toString() {
        return "Appointments{" +
                "subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", address='" + address + '\'' +
                ", year=" + year +
                ", mounth=" + mounth +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", type='" + type + '\'' +
                ", seen='" + seen + '\'' +
                '}';
    }
}
