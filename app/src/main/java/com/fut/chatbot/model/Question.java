/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import java.util.Date;
import java.util.List;

public class Question {

    public enum QuestionStatus {
        CREATED, REQUESTED, APPROVED
    };

    public enum QuestionExpiry {
        HOUR, DAY, WEEK, MONTH, SEMESTER, SESSION, NEVER
    };

    private int id;
    private String body;
    private List<QuestionTag> tags;
    private Answer answer;
    private Contributor contributor;
    private QuestionStatus status;
    private QuestionExpiry expiry;
    private Date setupTime;

    public Question() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<QuestionTag> getTags() {
        return tags;
    }

    public void setTags(List<QuestionTag> tags) {
        this.tags = tags;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public QuestionExpiry getExpiry() {
        return expiry;
    }

    public void setExpiry(QuestionExpiry expiry) {
        this.expiry = expiry;
    }

    public Date getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Date setupTime) {
        this.setupTime = setupTime;
    }

}
