package com.example.mingle.chat;

public class Chats {
    private String msg;
    private boolean msgFromUser;

    public Chats(String msg, boolean msgFromUser){
        this.msg = msg;
        this.msgFromUser = msgFromUser;
    }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public boolean isMsgFromUser() { return msgFromUser; }
    public void setMsgFromUser(boolean msgFromUser) { this.msgFromUser = msgFromUser; }
}
