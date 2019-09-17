package org.cocos2dx.javascript;


public class EventCaptcha {
    private boolean success;
    private String ticket;
    private String randstr;

    public EventCaptcha(boolean success, String ticket, String randstr) {
        this.success = success;
        this.ticket = ticket;
        this.randstr = randstr;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getRandstr() {
        return randstr;
    }

    public void setRandstr(String randstr) {
        this.randstr = randstr;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
