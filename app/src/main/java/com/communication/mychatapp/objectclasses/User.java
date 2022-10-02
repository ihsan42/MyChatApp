package com.communication.mychatapp.objectclasses;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User implements Comparable<User> {

    public String userName;
    public String userEmail;
    public String userUid;
    public Long userCreationTimeStamp;
    public Long userLastSignInTimeStamp;
    public Boolean isAdmin;

    public User() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public Long getUserCreationTimeStamp() {
        return userCreationTimeStamp;
    }

    public void setUserCreationTimeStamp(Long userCreationTimeStamp) {
        this.userCreationTimeStamp = userCreationTimeStamp;
    }

    public Long getUserLastSignInTimeStamp() {
        return userLastSignInTimeStamp;
    }

    public void setUserLastSignInTimeStamp(Long userLastSignInTimeStamp) {
        this.userLastSignInTimeStamp = userLastSignInTimeStamp;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userEmail", userEmail);
        result.put("userCreationTimeStamp", userCreationTimeStamp);
        result.put("userLastSignInTimeStamp", userLastSignInTimeStamp);
        result.put("isAdmin", isAdmin);
        result.put("userName", userName);
        result.put("userUid",userUid);
        return result;
    }

    @Override
    public int compareTo(User user) {
        return this.userUid.compareTo(user.userUid);
    }
}
