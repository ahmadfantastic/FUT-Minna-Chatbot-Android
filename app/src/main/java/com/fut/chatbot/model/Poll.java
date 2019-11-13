/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import java.util.Date;
import java.util.List;

public class Poll {

    public enum PollStatus {
        CREATED, REQUESTED, PROGRESS, FINISHED
    };

    public enum PollExpiry {
        DAY, DAY_3, WEEK, MONTH,
    };

    private int id;
    private String title;
    private String body;
    private List<PollTag> tags;
    private List<PollItem> items;
    private Contributor contributor;
    private PollStatus status;
    private PollExpiry expiry;
    private Date setupTime;
    private PollItem voted;

    public Poll() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<PollTag> getTags() {
        return tags;
    }

    public void setTags(List<PollTag> tags) {
        this.tags = tags;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public PollStatus getStatus() {
        return status;
    }

    public void setStatus(PollStatus status) {
        this.status = status;
    }

    public PollExpiry getExpiry() {
        return expiry;
    }

    public void setExpiry(PollExpiry expiry) {
        this.expiry = expiry;
    }

    public Date getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Date setupTime) {
        this.setupTime = setupTime;
    }

    public List<PollItem> getItems() {
        return items;
    }

    public void setItems(List<PollItem> items) {
        this.items = items;
    }

    public PollItem getVoted() {
        return voted;
    }

    public void setVoted(PollItem voted) {
        this.voted = voted;
    }
}
