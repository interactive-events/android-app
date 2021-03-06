package com.wordpress.interactiveevents.interactive_events;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by sofie on 14-11-13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Module {

    private String name;
    private String module;
    private String id;
    private String state;
    private Object customData;
    private String url;

    /*
        public void setType(String name) {
            this.name = name;
        }
        public void setModuleId(String moduleId) {
            this.module = moduleId;
        }
        public void setCustomData(List customData) {
            this.customData = customData;
        }
    */
    public String getState() { return state; }

    public String getName() {
        return name;
    }

    public String getModuleId() {
        return module;
    }

    public String getActivityId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Object getCustomData() {
        return customData;
    }

    public String toString() {
        return name;
    }
}