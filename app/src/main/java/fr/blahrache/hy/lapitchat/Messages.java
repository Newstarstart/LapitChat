package fr.blahrache.hy.lapitchat;

/**
 * Created by HP on 05/01/2018.
 */

public class Messages {
    private String subject;
    private String body;
    private String from;
    private String to;
    private String date;
    private String type;
    private String seen = "no";

    public Messages() {
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
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
        this.to = from;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
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
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
        this.from = to;
    }
}
