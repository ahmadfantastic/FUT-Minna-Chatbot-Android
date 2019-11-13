/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.fut.chatbot.util.Constants;
import java.util.Date;
import java.util.List;

public class Broadcast {

    public enum BroadcastStatus {
        CREATED, REQUESTED, SENT
    };

    public enum BroadcastExpiry {
        HOUR, HOUR_2, DAY, WEEK
    };

    private int id;
    private String body;
    private List<BroadcastTag> tags;
    private Contributor contributor;
    private BroadcastStatus status;
    private BroadcastExpiry expiry;
    private Date setupTime;

    public Broadcast() {
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

    public List<BroadcastTag> getTags() {
        return tags;
    }

    public void setTags(List<BroadcastTag> tags) {
        this.tags = tags;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public BroadcastStatus getStatus() {
        return status;
    }

    public void setStatus(BroadcastStatus status) {
        this.status = status;
    }

    public BroadcastExpiry getExpiry() {
        return expiry;
    }

    public void setExpiry(BroadcastExpiry expiry) {
        this.expiry = expiry;
    }

    public Date getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(Date setupTime) {
        this.setupTime = setupTime;
    }

}
