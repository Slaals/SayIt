package fr.utt.if26.sayit.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import fr.utt.if26.itsaysclient.ApiHttpClient;
import fr.utt.if26.itsaysclient.ItSaysEndpoints;
import fr.utt.if26.sayit.R;
import fr.utt.if26.sayit.fragment.ExpressionListFragment;
import fr.utt.if26.sayit.fragment.PublicationFragment;
import fr.utt.if26.sayit.fragment.PublishFragment;
import fr.utt.if26.sayit.utils.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ExpressionListFragment.ExpressionItemListener, PublishFragment.PublishButtonOnClickListener {

    TextView drawerUsernameView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verify wheter the user is logged in
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE);
        String permanentUserToken = sharedPreferences.getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);
        if (permanentUserToken == null) {
            clearLoginCredentials();
            Intent openLoginActivity = new Intent(this, LoginActivity.class);
            startActivity(openLoginActivity);
            /*
            finish() and return are crucial for kill MainActivity when LoginActivity opens
            Actually, kill MainActivity prevent the user to use the back button being into
            the the login screen and simply bypass it
            */
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Write username into drawer header
        String drawerUsername = sharedPreferences.getString(SharedPreferencesManager.USER_PREFERENCES_USERNAME, null);
        drawerUsernameView = (TextView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.drawerUsername);
        drawerUsernameView.setText(drawerUsername);

        // Display the expression list screen
        navigateToExpressionListScreen();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo_sayit);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                // Hide keyboard when opening the Drawer
                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void clearLoginCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save permanent token and username into shared preferences
        editor.remove(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN);
        editor.remove(SharedPreferencesManager.USER_PREFERENCES_USERNAME);

        editor.apply();
    }

    private void navigateToPublicationScreen(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        PublicationFragment publicationFragment = new PublicationFragment();
        publicationFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.mainContentLayout, publicationFragment);
        fragmentTransaction.commit();
    }

    private void navigateToPublishScreen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PublishFragment publishFragment = new PublishFragment();
        fragmentTransaction.replace(R.id.mainContentLayout, publishFragment);
        fragmentTransaction.commit();
    }

    private void navigateToExpressionListScreen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ExpressionListFragment expressionListFragment = new ExpressionListFragment();
        fragmentTransaction.replace(R.id.mainContentLayout, expressionListFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            fm.popBackStack();
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.drawerMenuNavigationPublish:
                navigateToPublishScreen();
                break;
            case R.id.drawerMenuNavigationPublications:
                navigateToExpressionListScreen();
                break;
            case R.id.drawerMenuNavigationLogout:
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesManager.USER_PREFERENCES, Context.MODE_PRIVATE);
                String accessToken = sharedPreferences.getString(SharedPreferencesManager.USER_PREFERENCES_PERMANENT_TOKEN, null);
                ItSaysEndpoints.UserEndpoints.logout(accessToken, getApplicationContext(), new ApiHttpClient.ApiCallFinished() {
                    @Override
                    public void onApiCallCompleted() {
                        /*
                        Calling onApiCallCompleted instead of onApiCallSuceeded is crucial
                        because if the user's token is invalid or the API is uncallable,
                        the user will be blocked in a logged-in screen
                        */
                        clearLoginCredentials();
                        Intent openLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(openLoginActivity);
                        /*
                        finish() is crucial for kill MainActivity when LoginActivity opens
                        Actually, kill MainActivity prevent the user to use the back button being into
                        the the login screen and simply bypass it
                        */
                        finish();
                    }

                    @Override
                    public void onApiCallSucceeded(JSONObject response) {
                    }

                    @Override
                    public void onApiCallFailed(JSONObject response) {
                    }
                });
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClickListener(String id) {
        navigateToPublicationScreen(id);
    }

    @Override
    public void onPublishButtonClick() {
        navigateToExpressionListScreen();
    }
}
