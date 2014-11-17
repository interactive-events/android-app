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

    private String moduleId;
    private String type;
    private List customData;

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCustomData(List customData) {
        this.customData = customData;
    }

    public String getModuleId() {
        return moduleId;
    }
    public String getType() {
        return type;
    }
    public List getCustomData() {
        return customData;
    }
    public String toString() { return moduleId+" "+type; }

}
