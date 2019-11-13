/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.model;

import com.fut.chatbot.util.Constants;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class Contributor {

    public enum ContributorStatus {
        ACTIVE, BLOCKED
    };

    public enum ContributorType {
        REGULAR, CLASS_REP, LECTURER, ASSOCIATION, SUPER
    };

    private int id;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String email;
    private String password;
    private Date registrationDate;
    private ContributorType type;
    private ContributorStatus status;
    private List<ContributorTag> tags;

    public Contributor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ContributorType getType() {
        return type;
    }

    public void setType(ContributorType type) {
        this.type = type;
    }

    public ContributorStatus getStatus() {
        return status;
    }

    public void setStatus(ContributorStatus status) {
        this.status = status;
    }

    public List<ContributorTag> getTags() {
        return tags;
    }

    public void setTags(List<ContributorTag> tags) {
        this.tags = tags;
    }

    public String getName(){
        return firstName + " " + lastName;
    }

}
