package com.wordpress.interactiveevents.interactive_events;

import android.content.SharedPreferences;
import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

/**
 * Created by sofie on 14-11-13.
 */
public class ModulesRequest extends SpringAndroidSpiceRequest<ModulesList> {

    private String eventId;

    public ModulesRequest(String eventId) {
        super(ModulesList.class);
        this.eventId = eventId;
    }

    @Override
    public ModulesList loadDataFromNetwork() throws Exception {

        String access_token = Storage.getAccessToken();
        String access_param = "?access_token="+access_token;
        // from apiary
        String url = String.format("http://private-582d6-interactiveevents.apiary-mock.com/events/%s/modules"+access_param, eventId);
        // from api
        String url2 = String.format("http://interactive-events.elasticbeanstalk.com/events/%s/activities"+access_param, eventId);
        Log.d("access_token", "In ModulesRequest url2="+url2);
        return getRestTemplate().getForObject(url2, ModulesResponse.class).getModulesList();
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     * @return
     */
    public String createCacheKey() {
        return "eventModules." + eventId;
    }
}
