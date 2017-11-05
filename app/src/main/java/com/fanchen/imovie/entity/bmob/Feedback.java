package com.fanchen.imovie.entity.bmob;

/**
 * Created by fanchen on 2017/11/4.
 */
public class Feedback extends BmobObj{

    public static final int TYPE_BUG = 0;
    public static final int TYPE_OTHER = 1;
    public static final int TYPE_SUGGEST = 2;

    private String email;
    private String content;
    private long time = System.currentTimeMillis();
    private int type;
    private User user;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
