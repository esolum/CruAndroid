package org.androidcru.crucentralcoast.presentation.views.subscriptions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.androidcru.crucentralcoast.R;

/**
 * @author Connor Batch
 *
 * Manage Subscriptions Activity
 */
public class SubscriptionActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_startup);

        //REVIEW magic strings
        getSupportActionBar().setTitle("Subscriptions");
        getSupportActionBar().setSubtitle("Select ministries of interest");
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SubscriptionsFragment()).commit();
    }
}
