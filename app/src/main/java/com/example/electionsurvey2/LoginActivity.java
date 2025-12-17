package com.example.electionsurvey2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Login Activity - Handles user authentication
 * Validates phone and password, calls backend API, and saves user session
 */
public class LoginActivity extends AppCompatActivity {

    // UI Components
    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView tvError;

    // Network
    private OkHttpClient client;

    // API Configuration
    private static final String BASE_URL = "http://143.110.252.32:4000"; // Remote server IP
    private static final String LOGIN_ENDPOINT = "/api/login";

    // SharedPreferences
    private static final String PREF_NAME = "ElectionSurveyPrefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize OkHttp client
        client = new OkHttpClient();

        // Auto-login disabled - user must login every time
        // if (isUserLoggedIn()) {
        //     navigateToAreaSelection();
        //     return;
        // }

        // Initialize UI components
        initializeViews();

        // Set login button click listener
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
    }

    /**
     * Handle login button click
     * Validates input and calls API
     */
    private void handleLogin() {
        // Hide previous error
        tvError.setVisibility(View.GONE);

        // Get input values
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(phone)) {
            showError("Please enter phone number");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError("Please enter password");
            etPassword.requestFocus();
            return;
        }

        // Call login API
        performLogin(phone, password);
    }

    /**
     * Perform login API call
     * @param phone User's phone number
     * @param password User's password
     */
    private void performLogin(String phone, String password) {
        // Show loading state
        showLoading(true);

        try {
            // Create JSON body
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("phone", phone);
            jsonBody.put("password", password);

            // Create request body
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

            // Build request
            Request request = new Request.Builder()
                    .url(BASE_URL + LOGIN_ENDPOINT)
                    .post(body)
                    .build();

            // Execute async request
            System.out.println("Attempting to connect to: " + BASE_URL + LOGIN_ENDPOINT);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Network error
                    System.err.println("Login API failure: " + e.getMessage());
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, 
                            "Unable to connect to server: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseBody = response.body().string();
                    
                    runOnUiThread(() -> {
                        showLoading(false);
                        handleLoginResponse(responseBody);
                    });
                }
            });

        } catch (JSONException e) {
            showLoading(false);
            showError("Error creating request");
        }
    }

    /**
     * Handle API response
     * @param responseBody JSON response from server
     */
    private void handleLoginResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                // Login successful
                JSONObject data = jsonResponse.getJSONObject("data");
                int userId = data.getInt("id");
                String userName = data.getString("name");
                String userPhone = data.getString("phone");

                // Save user data to SharedPreferences
                saveUserData(userId, userName, userPhone);

                // Navigate to next screen
                navigateToAreaSelection();

            } else {
                // Login failed
                String message = jsonResponse.optString("message", "Invalid phone or password");
                showError(message);
            }

        } catch (JSONException e) {
            showError("Invalid phone or password");
        }
    }

    /**
     * Save user data to SharedPreferences
     */
    private void saveUserData(int userId, String userName, String userPhone) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_PHONE, userPhone);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Check if user is already logged in
     */
    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Navigate to Area Selection screen
     */
    private void navigateToAreaSelection() {
        Intent intent = new Intent(LoginActivity.this, AreaSelectionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Show/hide loading state
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            btnLogin.setEnabled(false);
            btnLogin.setText("Logging in...");
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setEnabled(true);
            btnLogin.setText("Login");
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}

