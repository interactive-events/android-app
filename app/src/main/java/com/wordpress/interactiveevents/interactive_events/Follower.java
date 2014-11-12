package com.wordpress.interactiveevents.interactive_events;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by sofie on 14-11-12.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Follower {

    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}