package com.ab.hicarescanner.network.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Arjun Bhatt on 1/30/2020.
 */
public class LoginData extends RealmObject {
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Username")
    @PrimaryKey
    @Expose
    private String username;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("MobilePhone")
    @Expose
    private String mobilePhone;
    @SerializedName("EmployeeNumber")
    @Expose
    private String employeeNumber;

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("TicketID")
    @Expose
    private String ticketID;
    @SerializedName("Name")
    @Expose
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
