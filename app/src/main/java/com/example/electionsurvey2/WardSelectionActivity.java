package com.example.electionsurvey2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Ward Selection Activity
 * Displays list of wards for selected area from API
 */
public class WardSelectionActivity extends AppCompatActivity implements WardAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewWards;
    private LinearLayout emptyState;
    private TextView tvUserEmail;
    private TextView tvSelectedArea;

    // Data
    private List<Ward> wardList;
    private WardAdapter wardAdapter;
    private int areaId;
    private String areaName;

    // SharedPreferences
    private static final String PREF_NAME = "ElectionSurveyPrefs";
    private static final String KEY_WARD_ID = "ward_id";
    private static final String KEY_WARD_NAME = "ward_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ward_selection);

        // Get data from intent
        getIntentData();

        // Initialize views
        initializeViews();

        // Setup toolbar and drawer
        setupToolbar();
        setupDrawer();

        // Setup RecyclerView
        setupRecyclerView();

        // Update user info in drawer header
        updateUserInfo();

        // Display selected area name
        displaySelectedArea();

        // Load wards from API
        if (areaId != -1) {
            loadWards();
        } else {
            Toast.makeText(this, "Error: Area not selected", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Get area ID and name from intent
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            areaId = intent.getIntExtra("area_id", -1);
            areaName = intent.getStringExtra("area_name");
        }
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewWards = findViewById(R.id.recyclerViewWards);
        emptyState = findViewById(R.id.emptyState);
        tvSelectedArea = findViewById(R.id.tvSelectedArea);
        tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);

        wardList = new ArrayList<>();
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
        String userName = prefs.getString("user_name", "User");
        tvUserEmail.setText(userName);
    }

    /**
     * Display the selected area name
     */
    private void displaySelectedArea() {
        if (tvSelectedArea != null && areaName != null && !areaName.isEmpty()) {
            tvSelectedArea.setText(areaName);
        } else if (tvSelectedArea != null) {
            // Fallback: Get area name from SharedPreferences if not in intent
            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            String savedAreaName = prefs.getString("area_name", "Unknown Area");
            tvSelectedArea.setText(savedAreaName);
        }
    }

    /**
     * Setup RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        wardAdapter = new WardAdapter(wardList, this);
        recyclerViewWards.setAdapter(wardAdapter);
        recyclerViewWards.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Load wards from API for selected area
     */
    private void loadWards() {
        // Show loading
        showLoading(true);

        // Build endpoint with area ID
        String endpoint = ApiService.WARDS + areaId;

        // Make API request
        ApiService.get(endpoint, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Network error
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(WardSelectionActivity.this,
                            "Unable to connect to server",
                            Toast.LENGTH_LONG).show();
                    showEmptyState(true);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();

                runOnUiThread(() -> {
                    showLoading(false);
                    handleWardsResponse(responseBody);
                });
            }
        });
    }

    /**
     * Handle API response
     * @param responseBody JSON response from server
     */
    private void handleWardsResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                // Parse wards array
                JSONArray dataArray = jsonResponse.getJSONArray("data");
                wardList.clear();

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject wardJson = dataArray.getJSONObject(i);
                    int id = wardJson.getInt("id");
                    String wardName = wardJson.getString("ward_name");

                    Ward ward = new Ward(id, wardName);
                    wardList.add(ward);
                }

                // Update RecyclerView
                if (wardList.isEmpty()) {
                    showEmptyState(true);
                } else {
                    showEmptyState(false);
                    wardAdapter.updateWards(wardList);
                }

            } else {
                // API returned success: false
                String message = jsonResponse.optString("message", "Failed to load wards");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                showEmptyState(true);
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
            showEmptyState(true);
        }
    }

    /**
     * Handle ward item click
     * @param ward Selected ward
     */
    @Override
    public void onItemClick(Ward ward) {
        // Save selected ward to SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_WARD_ID, ward.getId());
        editor.putString(KEY_WARD_NAME, ward.getWardName());
        editor.apply();

        // Navigate to Survey Activity
        Intent intent = new Intent(WardSelectionActivity.this, SurveyActivity.class);
        intent.putExtra("ward_id", ward.getId());
        intent.putExtra("ward_name", ward.getWardName());
        startActivity(intent);
    }

    /**
     * Show/hide loading state
     * @param isLoading Loading state
     */
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    /**
     * Show/hide empty state
     * @param isEmpty Empty state
     */
    private void showEmptyState(boolean isEmpty) {
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewWards.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Allow going back to area selection
            super.onBackPressed();
        }
    }
}
