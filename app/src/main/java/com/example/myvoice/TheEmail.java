package com.example.myvoice;

public class TheEmail {
    public TheEmail(String msgFrom,String msgSubject, String msgBodyContent){
        this.msgFrom=msgFrom;
        this.msgSubject=msgSubject;
        this.msgBodyContent=msgBodyContent;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    private String msgFrom;

    public String getMsgSubject() {
        return msgSubject;
    }

    public void setMsgSubject(String msgSubject) {
        this.msgSubject = msgSubject;
    }

    private String msgSubject;

    public String getMsgBodyContent() {
        return msgBodyContent;
    }

    public void setMsgBodyContent(String msgBodyContent) {
        this.msgBodyContent = msgBodyContent;
    }

    private String msgBodyContent;
}
