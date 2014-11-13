package com.wordpress.interactiveevents.interactive_events;

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
        String url = String.format("http://private-582d6-interactiveevents.apiary-mock.com/events/%s/modules", eventId);
        return getRestTemplate().getForObject(url, ModulesResponse.class).getModulesList();
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
