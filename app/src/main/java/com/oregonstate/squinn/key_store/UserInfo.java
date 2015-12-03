package com.oregonstate.squinn.key_store;

import android.app.Application;

/**
 * Created by squinn on 12/1/15.
 */
public class UserInfo extends Application {
    private String personName;
    private String personId;

    public String getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
