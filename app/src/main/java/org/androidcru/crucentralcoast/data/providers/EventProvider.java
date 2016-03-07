package org.androidcru.crucentralcoast.data.providers;

import org.androidcru.crucentralcoast.data.models.CruEvent;
import org.androidcru.crucentralcoast.data.providers.util.RxComposeUtil;
import org.androidcru.crucentralcoast.data.services.CruApiService;

import java.util.ArrayList;

import rx.Observable;

public class EventProvider
{
    private static CruApiService mCruService = ApiProvider.getService();

    public static Observable<ArrayList<CruEvent>> requestEvents()
    {

        return mCruService.getEvents()
                .compose(RxComposeUtil.network());
    }

    public static Observable<CruEvent> requestCruEventByID(String id)
    {
        return mCruService.findSingleCruEvent(id)
                .compose(RxComposeUtil.network())
                .flatMap(cruevents -> {
                    return Observable.from(cruevents);
                });
    }
}
