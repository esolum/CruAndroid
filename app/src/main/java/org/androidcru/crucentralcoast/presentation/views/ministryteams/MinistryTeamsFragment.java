package org.androidcru.crucentralcoast.presentation.views.ministryteams;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.androidcru.crucentralcoast.R;
import org.androidcru.crucentralcoast.data.models.MinistryTeam;
import org.androidcru.crucentralcoast.data.providers.MinistryTeamProvider;
import org.androidcru.crucentralcoast.presentation.views.base.BaseSupportFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;

public class MinistryTeamsFragment extends BaseSupportFragment
{
    @BindView(R.id.subscription_list) RecyclerView subscriptionList;
    @BindView(R.id.progress) ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private MinistryTeamsAdapter ministryTeamsAdapter;
    private Observer<List<MinistryTeam>> observer;

    public MinistryTeamsFragment()
    {
        //REVIEW This could be written more efficiently with Observers.create()
        observer = new Observer<List<MinistryTeam>>()
        {
            @Override
            public void onCompleted()
            {
                toggleProgessBar(false);
            }

            @Override
            public void onError(Throwable e)
            {
            }

            @Override
            public void onNext(List<MinistryTeam> ministryTeams)
            {
                ministryTeamsAdapter = new MinistryTeamsAdapter(getActivity(), ministryTeams);
                subscriptionList.setAdapter(ministryTeamsAdapter);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_ministry_teams, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        ministryTeamsAdapter = new MinistryTeamsAdapter(getActivity(), new ArrayList<>());
        subscriptionList.setHasFixedSize(true);
        subscriptionList.setAdapter(ministryTeamsAdapter);

        layoutManager = new GridLayoutManager(getActivity(), 2);
        subscriptionList.setLayoutManager(layoutManager);

        getMinistryTeamsList();
    }

    private void toggleProgessBar(boolean isShown)
    {
        progressBar.setVisibility(isShown ? View.VISIBLE : View.GONE);
        subscriptionList.setVisibility(isShown ? View.GONE : View.VISIBLE);
    }

    public void getMinistryTeamsList()
    {
        toggleProgessBar(true);
        MinistryTeamProvider.requestMinistryTeams(this, observer);
    }

}
