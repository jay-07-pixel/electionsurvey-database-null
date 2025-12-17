package com.example.electionsurvey2;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.cardview.widget.CardView;

import com.google.android.material.navigation.NavigationView;

import com.google.android.material.button.MaterialButton;

/**
 * Success Activity
 * Displays success message after survey submission with animations
 */
public class SuccessActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView tvUserEmail;
    private CardView successIcon;
    private MaterialButton btnBackToHome, btnExit;

    private static final String PREF_NAME = "ElectionSurveyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        // Initialize views
        initializeViews();

        // Setup toolbar and drawer
        setupToolbar();
        setupDrawer();

        // Update user info in drawer header
        updateUserInfo();

        // Setup animations
        setupAnimations();

        // Setup button listeners
        setupButtons();
    }

    /**
     * Initialize all views
     */
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        successIcon = findViewById(R.id.successIcon);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnExit = findViewById(R.id.btnExit);
        
        if (navigationView != null) {
            tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);
        }
    }

    /**
     * Setup toolbar with drawer toggle
     */
    private void setupToolbar() {
        if (toolbar != null) {
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
    }

    /**
     * Setup navigation drawer
     */
    private void setupDrawer() {
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    /**
     * Update user information in drawer header
     */
    private void updateUserInfo() {
        if (tvUserEmail != null) {
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String userName = prefs.getString("user_name", "User");
            tvUserEmail.setText(userName);
        }
    }

    /**
     * Setup enter animations
     */
    private void setupAnimations() {
        // Success icon animation - scale and fade in
        successIcon.setScaleX(0f);
        successIcon.setScaleY(0f);
        successIcon.setAlpha(0f);

        successIcon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Rotation animation for checkmark effect
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(successIcon, "rotation", 0f, 360f);
        rotationAnimator.setDuration(800);
        rotationAnimator.setStartDelay(200);
        rotationAnimator.start();
    }

    /**
     * Setup button click listeners
     */
    private void setupButtons() {
        // Back to home - restart from login
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(SuccessActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Exit app
        btnExit.setOnClickListener(v -> {
            finishAffinity(); // Close all activities and exit app
        });
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
            // Navigate to Notes
            Intent intent = new Intent(this, NotesActivity.class);
            startActivity(intent);
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
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Disable back button - user must click a button to proceed
            // This prevents accidental navigation away from success screen
        }
    }
}





