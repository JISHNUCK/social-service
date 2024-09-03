package com.egov.socialservice;

import java.util.UUID;

public class Credential {


    UUID citizenid;
    String password;

    public void setCitizenid(UUID citizenid) {
        this.citizenid = citizenid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public UUID getCitizenid() {
        return citizenid;
    }
}
