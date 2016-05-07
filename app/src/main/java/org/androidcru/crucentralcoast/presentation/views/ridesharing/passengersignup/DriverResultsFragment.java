package org.androidcru.crucentralcoast.presentation.views.ridesharing.passengersignup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.androidcru.crucentralcoast.R;
import org.androidcru.crucentralcoast.data.models.Ride;
import org.androidcru.crucentralcoast.data.models.queries.Query;
import org.androidcru.crucentralcoast.data.providers.RideProvider;
import org.androidcru.crucentralcoast.presentation.util.DividerItemDecoration;
import org.androidcru.crucentralcoast.presentation.views.base.ListFragment;
import org.androidcru.crucentralcoast.presentation.views.forms.FormContentFragment;
import org.androidcru.crucentralcoast.presentation.views.forms.FormHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class DriverResultsFragment extends FormContentFragment
{
    @BindView(R.id.recyclerview) protected RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout) protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.informational_text) protected TextView informationalText;
    private View emptyView;

    private Query query;
    private List<Ride> results;
    private LatLng passengerLocation;

    private FormHolder formHolder;
    private Observer<List<Ride>> rideResultsObserver;

    public DriverResultsFragment()
    {
        results = new ArrayList<>();
        rideResultsObserver = new Observer<List<Ride>>()
        {
            @Override
            public void onCompleted()
            {
                swipeRefreshLayout.setRefreshing(false);
                if (results.isEmpty())
                {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else
                {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onNext(List<Ride> rides)
            {
                handleResults(rides);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.list_with_empty_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ViewStub emptyViewStub = (ViewStub) view.findViewById(R.id.empty_view_stub);
        emptyViewStub.setLayoutResource(R.layout.empty_with_alert);
        emptyView = emptyViewStub.inflate();

        unbinder = ButterKnife.bind(this, view);

        informationalText.setText(R.string.no_rides);

        ListFragment.setupSwipeRefreshLayout(swipeRefreshLayout);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
    }


    private void getRides()
    {
        swipeRefreshLayout.setRefreshing(true);
        results.clear();
        RideProvider.searchRides(this, rideResultsObserver, query,
                new Double[]{passengerLocation.latitude, passengerLocation.longitude});
    }

    private void handleResults(List<Ride> results)
    {
        this.results = results;
        recyclerView.setAdapter(new DriverResultsAdapter(this, formHolder, results));
    }

    @Override
    public void setupData(FormHolder formHolder)
    {
        this.formHolder = formHolder;
        formHolder.setTitle(getString(R.string.passenger_pick_driver));
        query = (Query) formHolder.getDataObject(PassengerSignupActivity.QUERY);
        passengerLocation = (LatLng) formHolder.getDataObject(PassengerSignupActivity.LATLNG);

        formHolder.setNavigationVisibility(View.VISIBLE);
        formHolder.setNextVisibility(View.GONE);
        formHolder.setPreviousVisibility(View.VISIBLE);
        getRides();
    }
}
