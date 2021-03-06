package com.wordpress.interactiveevents.interactive_events;

import android.util.Log;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by sofie on 14-11-13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModulesResponse {

    @JsonProperty("activities")
    private ModulesList modulesList;

    /*public void setModulesList(ModulesList modulesList) {
        this.modulesList = modulesList;
    }*/

    public ModulesList getModulesList() {
        return modulesList;
    }
}
