package com.fut.chatbot.retrofit;

/**
 *
 * @author ahmad
 */
public class Result {

    public enum Status {
        SUCCESS, INVALID_KEY, INVALID_USER, INVALID_CODE, ERROR
    }

    private Status status;
    private Object data;

    public Result(Status status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
