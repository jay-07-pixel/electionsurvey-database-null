package com.example.electionsurvey2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.view.MenuItem;

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
 * Area Selection Activity
 * Displays list of areas from API and allows user to select one
 */
public class AreaSelectionActivity extends AppCompatActivity implements AreaAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewAreas;
    private LinearLayout emptyState;
    private TextView tvUserEmail;

    // Data
    private List<Area> areaList;
    private AreaAdapter areaAdapter;

    // SharedPreferences
    private static final String PREF_NAME = "ElectionSurveyPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_AREA_ID = "area_id";
    private static final String KEY_AREA_NAME = "area_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_selection);

        // Initialize views
        initializeViews();

        // Setup toolbar and drawer
        setupToolbar();
        setupDrawer();

        // Setup RecyclerView
        setupRecyclerView();

        // Update user info in drawer header
        updateUserInfo();

        // Load areas from API
        loadAreas();
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        recyclerViewAreas = findViewById(R.id.recyclerViewAreas);
        emptyState = findViewById(R.id.emptyState);
        tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);

        areaList = new ArrayList<>();
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
     * Setup RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        areaAdapter = new AreaAdapter(areaList, this);
        recyclerViewAreas.setAdapter(areaAdapter);
        recyclerViewAreas.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Load areas from API
     */
    private void loadAreas() {
        // Show loading
        showLoading(true);

        // Make API request
        ApiService.get(ApiService.AREAS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Network error
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AreaSelectionActivity.this,
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
                    handleAreasResponse(responseBody);
                });
            }
        });
    }

    /**
     * Handle API response
     * @param responseBody JSON response from server
     */
    private void handleAreasResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                // Parse areas array
                JSONArray dataArray = jsonResponse.getJSONArray("data");
                areaList.clear();

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject areaJson = dataArray.getJSONObject(i);
                    int id = areaJson.getInt("id");
                    String areaName = areaJson.getString("area_name");

                    Area area = new Area(id, areaName);
                    areaList.add(area);
                }

                // Update RecyclerView
                if (areaList.isEmpty()) {
                    showEmptyState(true);
                } else {
                    showEmptyState(false);
                    areaAdapter.updateAreas(areaList);
                }

            } else {
                // API returned success: false
                String message = jsonResponse.optString("message", "Failed to load areas");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                showEmptyState(true);
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
            showEmptyState(true);
        }
    }

    /**
     * Handle area item click
     * @param area Selected area
     */
    @Override
    public void onItemClick(Area area) {
        // Save selected area to SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_AREA_ID, area.getId());
        editor.putString(KEY_AREA_NAME, area.getAreaName());
        editor.apply();

        // Navigate to Ward Selection Activity
        Intent intent = new Intent(AreaSelectionActivity.this, WardSelectionActivity.class);
        intent.putExtra("area_id", area.getId());
        intent.putExtra("area_name", area.getAreaName());
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
        recyclerViewAreas.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    /**
     * Handle navigation menu item clicks
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_survey) {
            // Already on Area Selection (start of survey flow)
            drawerLayout.closeDrawer(GravityCompat.START);
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
            // Disable back button to prevent going back to login
            // User must complete the survey flow
        }
    }
}
