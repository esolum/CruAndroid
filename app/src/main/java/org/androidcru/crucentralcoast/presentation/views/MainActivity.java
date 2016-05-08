package org.androidcru.crucentralcoast.presentation.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.androidcru.crucentralcoast.AppConstants;
import org.androidcru.crucentralcoast.CruApplication;
import org.androidcru.crucentralcoast.R;
import org.androidcru.crucentralcoast.data.models.CruUser;
import org.androidcru.crucentralcoast.data.providers.UserProvider;
import org.androidcru.crucentralcoast.presentation.providers.FacebookProvider;
import org.androidcru.crucentralcoast.presentation.views.base.BaseAppCompatActivity;
import org.androidcru.crucentralcoast.presentation.views.communitygroups.CommunityGroupsFragment;
import org.androidcru.crucentralcoast.presentation.views.events.EventsFragment;
import org.androidcru.crucentralcoast.presentation.views.feed.FeedFragment;
import org.androidcru.crucentralcoast.presentation.views.ministryteams.MinistryTeamsFragment;
import org.androidcru.crucentralcoast.presentation.views.resources.ResourcesFragment;
import org.androidcru.crucentralcoast.presentation.views.ridesharing.myrides.MyRidesFragment;
import org.androidcru.crucentralcoast.presentation.views.subscriptions.SubscriptionActivity;
import org.androidcru.crucentralcoast.presentation.views.summermissions.SummerMissionsFragment;
import org.androidcru.crucentralcoast.presentation.views.videos.VideosFragment;

import java.util.Collections;

import rx.Observer;
import rx.observers.Observers;

public class MainActivity extends BaseAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private static MainActivity self;
    private NavigationView navigationView;
    private ConstructionFragment constructionFragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        self = this;

        constructionFragment = new ConstructionFragment();
        if(savedInstanceState == null)
        {
            switchToFeed();
        }

        checkPlayServicesCode();
        setupAutoFillData();
    }

    /**
     * If this is the first time the
     */
    private void setupAutoFillData()
    {
        if (!CruApplication.getSharedPreferences().getBoolean(AppConstants.FIRST_LAUNCH, false))
        {
            RxPermissions.getInstance(this)
                .request(Manifest.permission.READ_PHONE_STATE)
                .subscribe(granted -> {
                    if (granted)
                    {
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        String userPhoneNumber = telephonyManager.getLine1Number();
                        if (userPhoneNumber != null)
                            userPhoneNumber = userPhoneNumber.substring(userPhoneNumber.length() - 10, userPhoneNumber.length());

                        CruApplication.getSharedPreferences().edit().putString(AppConstants.USER_PHONE_NUMBER, userPhoneNumber).apply();

                        Observer<CruUser> observer = Observers.create(cruUser -> {
                            SharedPreferences userSharedPreferences = CruApplication.getSharedPreferences();
                            userSharedPreferences.edit().putString(AppConstants.USER_NAME, cruUser.name.firstName + " " + cruUser.name.lastName).commit();
                            userSharedPreferences.edit().putString(AppConstants.USER_EMAIL, cruUser.email).commit();
                        });

                        UserProvider.requestCruUser(this, observer, userPhoneNumber);
                    }
                });
        }
        CruApplication.getSharedPreferences().edit().putBoolean(AppConstants.FIRST_LAUNCH, true).apply();
    }

    private void checkPlayServicesCode()
    {
        int playServicesCode = CruApplication.getSharedPreferences().getInt(AppConstants.PLAY_SERVICES, ConnectionResult.SUCCESS);
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        if(playServicesCode != ConnectionResult.SUCCESS)
        {
            if(apiAvailability.isUserResolvableError(playServicesCode))
            {
                apiAvailability.getErrorDialog(this, playServicesCode, AppConstants.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }
            else
            {
                //REVIEW magic strings
                new AlertDialog.Builder(this)
                        .setTitle("Unsupported Device")
                        .setMessage("Sorry but your device does not support Google Play Services")
                        .setOnCancelListener(dialog -> finish())
                        .show();
            }
        }
    }

    private void spawnConstructionFragment()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, constructionFragment).commit();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, SubscriptionActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.nav_feed:
                toolbar.setTitle(R.string.nav_feed);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new FeedFragment()).commit();
                break;
            case R.id.nav_events:
                toolbar.setTitle(R.string.nav_events);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new EventsFragment()).commit();
                break;
            case R.id.nav_my_rides:
                toolbar.setTitle(R.string.nav_my_rides);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new MyRidesFragment()).commit();
                break;
            case R.id.nav_summer_missions:
                toolbar.setTitle(R.string.nav_summer_missions);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new SummerMissionsFragment()).commit();
                break;
            case R.id.nav_community_groups:
                toolbar.setTitle(R.string.community_groups);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new CommunityGroupsFragment()).commit();
                break;
            case R.id.nav_ministry_teams:
                toolbar.setTitle(R.string.ministry_teams);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new MinistryTeamsFragment()).commit();
                break;
            case R.id.nav_resources:
                toolbar.setTitle(R.string.resources);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new ResourcesFragment()).commit();
                break;
            case R.id.nav_videos:
                toolbar.setTitle(R.string.nav_videos);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new VideosFragment()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void switchToMyRides(Bundle b)
    {
        navigationView.setCheckedItem(R.id.nav_my_rides);
        toolbar.setTitle(R.string.nav_my_rides);
        MyRidesFragment fragment = new MyRidesFragment();
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    //REVIEW this should be used in onNavigationItemSelected
    public void switchToEvents()
    {
        navigationView.setCheckedItem(R.id.nav_events);
        toolbar.setTitle(R.string.nav_events);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new EventsFragment()).commit();
    }

    public void switchToFeed()
    {
        navigationView.setCheckedItem(R.id.nav_feed);
        toolbar.setTitle(R.string.nav_feed);
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new FeedFragment()).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookProvider.tokenReceived(requestCode, resultCode, data);
    }

    public static void loginWithFacebook()
    {
        //REVIEW magic string, AppConstants
        LoginManager.getInstance().logInWithPublishPermissions(self, Collections.singletonList("rsvp_event"));
    }
}
