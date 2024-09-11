package com.egov.socialservice;

import java.util.UUID;

public class SocialEvent1 {

    String type;
    UUID userid;

    public void setType(String type) {
        this.type = type;
    }

    public void setUsername(UUID username) {
        this.userid = username;
    }

    public String getType() {
        return type;
    }

    public UUID getUsername() {
        return userid;
    }
}
