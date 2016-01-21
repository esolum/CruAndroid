package org.androidcru.crucentralcoast.presentation.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.androidcru.crucentralcoast.CruApplication;
import org.androidcru.crucentralcoast.R;
import org.androidcru.crucentralcoast.presentation.views.fragments.SubscriptionsFragment;

public class SubscriptionStartupActivity extends AppCompatActivity
{

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_startup);

        this.mSharedPreferences = getSharedPreferences(CruApplication.retrievePackageName(), Context.MODE_PRIVATE);
        getSupportActionBar().setTitle("Subscriptions");
        getSupportActionBar().setSubtitle("Select ministries of interest");
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SubscriptionsFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.subscription_startup_menu, menu);

        Drawable drawable = menu.findItem(R.id.action_done).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.action_done).setIcon(drawable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch(item.getItemId())
        {
            case R.id.action_done:
                mSharedPreferences.edit().putBoolean(CruApplication.FIRST_LAUNCH, true).apply();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}