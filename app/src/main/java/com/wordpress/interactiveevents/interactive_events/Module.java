package com.wordpress.interactiveevents.interactive_events;

import android.util.ArrayMap;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by sofie on 14-11-13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Module {

    private String id;
    private String type;
    private List customData;

    public void setId(String id) {
        this.id = id;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCustomData(List customData) {
        this.customData = customData;
    }

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public List getCustomData() {
        return customData;
    }
    public String toString() { return id+" "+type; }

}
