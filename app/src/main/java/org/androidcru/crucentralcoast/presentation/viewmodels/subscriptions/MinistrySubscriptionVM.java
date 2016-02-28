package org.androidcru.crucentralcoast.presentation.viewmodels.subscriptions;

import android.content.SharedPreferences;

import org.androidcru.crucentralcoast.CruApplication;
import org.androidcru.crucentralcoast.data.models.MinistrySubscription;
import org.androidcru.crucentralcoast.notifications.RegistrationIntentService;

public class MinistrySubscriptionVM
{
    public String campusName;
    public MinistrySubscription ministry;
    private Boolean isSubscribed;
    private SharedPreferences sharedPreferences = CruApplication.getSharedPreferences();

    public MinistrySubscriptionVM(String campusName, MinistrySubscription ministry)
    {
        this.campusName = campusName;
        this.ministry = ministry;
    }


    public boolean getIsSubscribed()
    {
        if(isSubscribed == null)
        {
            isSubscribed = sharedPreferences.getBoolean(ministry.mSubscriptionId, false);
        }
        return isSubscribed;
    }

    public void setIsSubscribed(boolean isSubscribed)
    {
        this.isSubscribed = isSubscribed;
        if(isSubscribed)
            RegistrationIntentService.subscribeToMinistry(ministry.mSubscriptionId);
        else
            RegistrationIntentService.unsubscribeToMinistry(ministry.mSubscriptionId);
        sharedPreferences.edit().putBoolean(ministry.mSubscriptionId, isSubscribed).commit();
    }
}
