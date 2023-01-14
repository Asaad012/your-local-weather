package org.thosp.yourlocalweather;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.thosp.yourlocalweather.help.HelpActivity;
import org.thosp.yourlocalweather.model.Location;
import org.thosp.yourlocalweather.model.LocationsDbHelper;
import org.thosp.yourlocalweather.service.ReconciliationDbService;
import org.thosp.yourlocalweather.service.UpdateWeatherService;
import org.thosp.yourlocalweather.service.WeatherRequestDataHolder;
import org.thosp.yourlocalweather.settings.fragments.AboutPreferenceFragment;
import org.thosp.yourlocalweather.utils.AppPreference;
import org.thosp.yourlocalweather.utils.ForecastUtil;
import org.thosp.yourlocalweather.utils.LanguageUtil;
import org.thosp.yourlocalweather.utils.PreferenceUtil;
import org.thosp.yourlocalweather.utils.Utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.thosp.yourlocalweather.utils.LogToFile.appendLog;

public abstract class BaseActivity extends AppCompatActivity {

    private final String TAG = "BaseActivity";

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private TextView mHeaderCity;
    protected LocationsDbHelper locationsDbHelper;
    protected Location currentLocation;
    protected TextView localityView;

    private Messenger reconciliationDbService;
    private Lock reconciliationDbServiceLock = new ReentrantLock();
    private Queue<Message> reconciliationDbUnsentMessages = new LinkedList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindReconciliationDbService();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        getToolbar();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtil.setLanguage(base, PreferenceUtil.getLanguage(base)));
    }

    public void switchLocation(View arg0) {
        int newLocationOrderId = 1 + currentLocation.getOrderId();
        currentLocation = locationsDbHelper.getLocationByOrderId(newLocationOrderId);

        if (currentLocation == null) {
            newLocationOrderId = 0;
            currentLocation = locationsDbHelper.getLocationByOrderId(newLocationOrderId);
            if ((currentLocation.getOrderId() == 0) && !currentLocation.isEnabled() && (locationsDbHelper.getAllRows().size() > 1)) {
                newLocationOrderId++;
                currentLocation = locationsDbHelper.getLocationByOrderId(newLocationOrderId);
            }
        }

        AppPreference.setCurrentLocationId(this, currentLocation);
        localityView.setText(Utils.getCityAndCountry(this, newLocationOrderId));
        updateUI();
    }

    protected abstract void updateUI();

    private void setupNavDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (mDrawerLayout == null) {
            return;
        }
        mDrawerToggle = new ActionBarDrawerToggle(this,
                                                  mDrawerLayout,
                                                  mToolbar,
                                                  R.string.navigation_drawer_open,
                                                  R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        configureNavView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerLayout != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    private void configureNavView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(navigationViewListener);

        View headerLayout = navigationView.getHeaderView(0);
        mHeaderCity = (TextView) headerLayout.findViewById(R.id.nav_header_city);
        //mHeaderCity.setText(Utils.getCityAndCountry(this));
    }

    private NavigationView.OnNavigationItemSelectedListener navigationViewListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_menu_current_weather:
                            startActivity(new Intent(BaseActivity.this, MainActivity.class));
                            break;
                        case R.id.nav_menu_graphs:
                            createBackStack(new Intent(BaseActivity.this,
                                                       GraphsActivity.class));
                            break;
                        case R.id.nav_menu_weather_forecast:
                            createBackStack(new Intent(BaseActivity.this,
                                                       WeatherForecastActivity.class));
                            break;
                        case R.id.nav_settings:
                            createBackStack(new Intent(BaseActivity.this,
                                                       SettingsActivity.class));
                            break;
                        case R.id.nav_about:
                            Intent intent = new Intent(BaseActivity.this,
                                    SettingsActivity.class);
                            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                                    AboutPreferenceFragment.class.getName());
                            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_TITLE,
                                    R.string.preference_title_activity_about);
                            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_SHORT_TITLE,
                                    R.string.preference_title_activity_about);
                            createBackStack(intent);
                            break;
                        case R.id.nav_menu_help:
                            createBackStack(new Intent(BaseActivity.this,
                                    HelpActivity.class));
                            break;
                        case R.id.nav_feedback:
                            Intent sendMessage = new Intent(Intent.ACTION_SEND);
                            sendMessage.setType("message/rfc822");
                            sendMessage.putExtra(Intent.EXTRA_EMAIL, new String[]{
                                    getResources().getString(R.string.feedback_email)});
                            try {
                                startActivity(Intent.createChooser(sendMessage, "Send feedback"));
                            } catch (android.content.ActivityNotFoundException e) {
                                Toast.makeText(BaseActivity.this, "Communication app not found",
                                               Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }

                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
            };

    private void createBackStack(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder builder = TaskStackBuilder.create(this);
            builder.addNextIntentWithParentStack(intent);
            builder.startActivities();
        } else {
            startActivity(intent);
            finish();
        }
    }

    protected Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                setSupportActionBar(mToolbar);
            }
        }

        return mToolbar;
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDraw();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDraw() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @NonNull
    protected ProgressDialog getProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.isIndeterminate();
        dialog.setMessage(getString(R.string.load_progress));
        dialog.setCancelable(false);
        return dialog;
    }

    protected void sendMessageToWeatherForecastService(Long locationId) {
        if (!ForecastUtil.shouldUpdateForecast(this, locationId, UpdateWeatherService.WEATHER_FORECAST_TYPE)) {
            return;
        }
        sendMessageToWeatherForecastService(locationId, null);
    }

    protected void sendMessageToWeatherForecastService(Long locationId, String updateSource) {
        Intent intent = new Intent("android.intent.action.START_WEATHER_UPDATE");
        intent.setPackage("org.thosp.yourlocalweather");
        intent.putExtra("weatherRequest", new WeatherRequestDataHolder(locationId, updateSource, UpdateWeatherService.START_WEATHER_FORECAST_UPDATE));
        startService(intent);
    }

    protected void sendMessageToLongWeatherForecastService(Long locationId, String updateSource) {
        Intent intent = new Intent("android.intent.action.START_WEATHER_UPDATE");
        intent.setPackage("org.thosp.yourlocalweather");
        intent.putExtra("weatherRequest", new WeatherRequestDataHolder(locationId, updateSource, UpdateWeatherService.START_LONG_WEATHER_FORECAST_UPDATE));
        startService(intent);
    }

    protected void sendMessageToReconciliationDbService(boolean force) {
        appendLog(this,
                TAG,
                "going run reconciliation DB service");
        reconciliationDbServiceLock.lock();
        try {
            Message msg = Message.obtain(
                    null,
                    ReconciliationDbService.START_RECONCILIATION,
                    force?1:0
            );
            if (checkIfReconciliationDbServiceIsNotBound()) {
                //appendLog(getBaseContext(), TAG, "WidgetIconService is still not bound");
                reconciliationDbUnsentMessages.add(msg);
                return;
            }
            //appendLog(getBaseContext(), TAG, "sendMessageToService:");
            reconciliationDbService.send(msg);
        } catch (RemoteException e) {
            appendLog(getBaseContext(), TAG, e.getMessage(), e);
        } finally {
            reconciliationDbServiceLock.unlock();
        }
    }

    private boolean checkIfReconciliationDbServiceIsNotBound() {
        if (reconciliationDbService != null) {
            return false;
        }
        try {
            bindReconciliationDBService();
        } catch (Exception ie) {
            appendLog(getBaseContext(), TAG, "weatherForecastServiceIsNotBound interrupted:", ie);
        }
        return (reconciliationDbService == null);
    }

    private void bindReconciliationDBService() {
        getApplicationContext().bindService(
                new Intent(getApplicationContext(), ReconciliationDbService.class),
                reconciliationDbServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    private void unbindReconciliationDbService() {
        if (reconciliationDbService == null) {
            return;
        }
        getApplicationContext().unbindService(reconciliationDbServiceConnection);
    }

    private ServiceConnection reconciliationDbServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binderService) {
            reconciliationDbService = new Messenger(binderService);
            reconciliationDbServiceLock.lock();
            try {
                while (!reconciliationDbUnsentMessages.isEmpty()) {
                    reconciliationDbService.send(reconciliationDbUnsentMessages.poll());
                }
            } catch (RemoteException e) {
                appendLog(getBaseContext(), TAG, e.getMessage(), e);
            } finally {
                reconciliationDbServiceLock.unlock();
            }
        }
        public void onServiceDisconnected(ComponentName className) {
            reconciliationDbService = null;
        }
    };
}
