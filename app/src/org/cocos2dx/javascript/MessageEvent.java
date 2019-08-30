package org.cocos2dx.javascript;


public  class MessageEvent {
    private String jsUrl;



    public MessageEvent(String jsUrl) {
        this.jsUrl = jsUrl;
    }

    public String getJsUrl() {
        return jsUrl;
    }

    public void setJsUrl(String jsUrl) {
        this.jsUrl = jsUrl;
    }
}
