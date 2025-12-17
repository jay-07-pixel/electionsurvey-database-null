package com.example.electionsurvey2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * Notes Activity - Coming Soon placeholder
 */
public class NotesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView tvUserEmail;

    private static final String PREF_NAME = "ElectionSurveyPrefs";
    private static final String KEY_USER_NAME = "user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Initialize views
        initializeViews();

        // Setup toolbar and drawer
        setupToolbar();
        setupDrawer();

        // Update user info in drawer header
        updateUserInfo();
    }

    /**
     * Initialize all views
     */
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);
    }

    /**
     * Setup toolbar with drawer toggle
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Setup navigation drawer
     */
    private void setupDrawer() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Update user information in drawer header
     */
    private void updateUserInfo() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String userName = prefs.getString(KEY_USER_NAME, "User");
        tvUserEmail.setText(userName);
    }

    /**
     * Handle navigation menu item clicks
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_survey) {
            // Navigate to Area Selection (start of survey flow)
            Intent intent = new Intent(this, AreaSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_notes) {
            // Already on Notes page
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_settings) {
            // Navigate to Settings
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
