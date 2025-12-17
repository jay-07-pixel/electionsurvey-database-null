package com.example.electionsurvey2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import com.google.android.material.button.MaterialButton;

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
 * Survey Activity
 * Displays survey questions dynamically and collects responses
 */
public class SurveyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView tvQuestionNumber, tvQuestionText;
    private LinearLayout answerContainer;
    private MaterialButton btnNext, btnSubmit;
    private TextView tvUserEmail;

    // Data
    private List<Question> questionList;
    private List<Answer> answerList;
    private int currentQuestionIndex = 0;

    // Current question UI elements
    private EditText currentEditText;
    private RadioGroup currentRadioGroup;
    private List<CheckBox> currentCheckBoxes;

    // User data
    private int userId, areaId, wardId;
    private static final int SURVEY_ID = 1;

    // SharedPreferences
    private static final String PREF_NAME = "ElectionSurveyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        // Initialize views
        initializeViews();

        // Setup toolbar and drawer
        setupToolbar();
        setupDrawer();

        // Update user info in drawer header
        updateUserInfo();

        // Get user data from SharedPreferences
        loadUserData();

        // Initialize lists
        questionList = new ArrayList<>();
        answerList = new ArrayList<>();

        // Load survey questions from API
        loadSurveyQuestions();

        // Setup button listeners
        setupButtonListeners();
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        answerContainer = findViewById(R.id.answerContainer);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);

        currentCheckBoxes = new ArrayList<>();
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
     * Load user data from SharedPreferences
     */
    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        areaId = prefs.getInt("area_id", -1);
        wardId = prefs.getInt("ward_id", -1);
    }

    /**
     * Setup button click listeners
     */
    private void setupButtonListeners() {
        btnNext.setOnClickListener(v -> handleNext());
        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    /**
     * Load survey questions from API
     */
    private void loadSurveyQuestions() {
        showLoading(true);

        String endpoint = ApiService.SURVEY_QUESTIONS + SURVEY_ID + "/questions";

        ApiService.get(endpoint, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(SurveyActivity.this,
                            "Unable to connect to server",
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();

                runOnUiThread(() -> {
                    showLoading(false);
                    parseQuestions(responseBody);
                });
            }
        });
    }

    /**
     * Parse questions from JSON response
     */
    private void parseQuestions(String responseBody) {
        try {
            // Log the raw response for debugging
            System.out.println("Survey API Response: " + responseBody);
            
            JSONObject jsonResponse = new JSONObject(responseBody);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                JSONArray dataArray = jsonResponse.getJSONArray("data");
                questionList.clear();

                for (int i = 0; i < dataArray.length(); i++) {
                    try {
                        JSONObject questionJson = dataArray.getJSONObject(i);
                        
                        int id = questionJson.getInt("id");
                        String questionText = questionJson.getString("question_text");
                        String type = questionJson.getString("type");

                        // Parse options - handle empty or missing options array
                        List<Option> options = new ArrayList<>();
                        if (questionJson.has("options")) {
                            JSONArray optionsArray = questionJson.getJSONArray("options");
                            for (int j = 0; j < optionsArray.length(); j++) {
                                JSONObject optionJson = optionsArray.getJSONObject(j);
                                int optionId = optionJson.getInt("id");
                                String optionText = optionJson.getString("option_text");
                                options.add(new Option(optionId, optionText));
                            }
                        }
                        // If options key is missing, options list remains empty (for text questions)

                        Question question = new Question(id, questionText, type, options);
                        questionList.add(question);
                        
                    } catch (JSONException e) {
                        // Log error for this specific question but continue parsing others
                        System.err.println("Error parsing question " + i + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // Display first question
                if (!questionList.isEmpty()) {
                    displayQuestion(0);
                } else {
                    Toast.makeText(this, "No questions available", Toast.LENGTH_LONG).show();
                }

            } else {
                String message = jsonResponse.optString("message", "Failed to load questions");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            // More detailed error message
            String errorMsg = "Error parsing questions: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Catch any other unexpected errors
            String errorMsg = "Unexpected error: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Display a question by index
     */
    private void displayQuestion(int index) {
        if (index < 0 || index >= questionList.size()) {
            return;
        }

        currentQuestionIndex = index;
        Question question = questionList.get(index);

        // Update question number
        tvQuestionNumber.setText("Question " + (index + 1) + " of " + questionList.size());

        // Update question text
        tvQuestionText.setText(question.getQuestionText());

        // Clear previous answer UI
        answerContainer.removeAllViews();
        currentEditText = null;
        currentRadioGroup = null;
        currentCheckBoxes.clear();

        // Create dynamic UI based on question type
        switch (question.getType()) {
            case "text":
                createTextInput(question);
                break;
            case "single":
                createSingleChoice(question.getOptions());
                break;
            case "multiple":
                createMultipleChoice(question.getOptions());
                break;
        }

        // Show/hide buttons
        if (index == questionList.size() - 1) {
            btnNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }
    }

    /**
     * Create text input for text questions
     * Applies input restrictions based on question content (name = alphabets only, age = numbers only)
     */
    private void createTextInput(Question question) {
        currentEditText = new EditText(this);
        currentEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        currentEditText.setPadding(16, 16, 16, 16);
        currentEditText.setBackgroundResource(android.R.drawable.edit_text);
        currentEditText.setTextSize(16);

        // Get question text in lowercase for checking
        String questionText = question.getQuestionText().toLowerCase();

        // Check if question is asking for name
        if (questionText.contains("name") || questionText.contains("full name")) {
            // Name field - only alphabets and spaces allowed
            currentEditText.setHint("Enter your name");
            currentEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            // Filter to allow only alphabets and spaces
            currentEditText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            char c = source.charAt(i);
                            // Allow alphabets (A-Z, a-z) and spaces
                            if (!Character.isLetter(c) && c != ' ') {
                                return "";
                            }
                        }
                        return null; // Accept the input
                    }
                }
            });
        }
        // Check if question is asking for age
        else if (questionText.contains("age")) {
            // Age field - only numbers allowed
            currentEditText.setHint("Enter your age");
            currentEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            // Filter to allow only digits
            currentEditText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            char c = source.charAt(i);
                            // Allow only digits (0-9)
                            if (!Character.isDigit(c)) {
                                return "";
                            }
                        }
                        return null; // Accept the input
                    }
                }
            });
        }
        // Check if question is asking for mobile/phone number
        else if (questionText.contains("mobile") || questionText.contains("phone") || 
                 questionText.contains("contact") || (questionText.contains("number") && 
                 (questionText.contains("mobile") || questionText.contains("phone") || 
                  questionText.contains("contact")))) {
            // Mobile/Phone number field - only numbers allowed, max 10 digits
            currentEditText.setHint("Enter mobile number (10 digits)");
            currentEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            // Filter to allow only digits and limit to 10 digits
            currentEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(10), // Maximum 10 digits
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            char c = source.charAt(i);
                            // Allow only digits (0-9)
                            if (!Character.isDigit(c)) {
                                return "";
                            }
                        }
                        return null; // Accept the input
                    }
                }
            });
        }
        // Default text input for other questions
        else {
            currentEditText.setHint("Enter your answer here");
            currentEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        }

        answerContainer.addView(currentEditText);
    }

    /**
     * Create radio buttons for single choice questions
     */
    private void createSingleChoice(List<Option> options) {
        currentRadioGroup = new RadioGroup(this);
        currentRadioGroup.setOrientation(RadioGroup.VERTICAL);
        currentRadioGroup.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        for (Option option : options) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(option.getId());
            radioButton.setText(option.getOptionText());
            radioButton.setTextSize(16);
            radioButton.setPadding(16, 16, 16, 16);
            radioButton.setTextColor(Color.BLACK);

            currentRadioGroup.addView(radioButton);
        }

        answerContainer.addView(currentRadioGroup);
    }

    /**
     * Create checkboxes for multiple choice questions
     */
    private void createMultipleChoice(List<Option> options) {
        for (Option option : options) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(option.getId());
            checkBox.setText(option.getOptionText());
            checkBox.setTextSize(16);
            checkBox.setPadding(16, 16, 16, 16);
            checkBox.setTextColor(Color.BLACK);

            currentCheckBoxes.add(checkBox);
            answerContainer.addView(checkBox);
        }
    }

    /**
     * Handle Next button click
     */
    private void handleNext() {
        if (saveCurrentAnswer()) {
            // Move to next question
            displayQuestion(currentQuestionIndex + 1);
        }
    }

    /**
     * Save current answer
     * @return true if answer is valid and saved
     */
    private boolean saveCurrentAnswer() {
        Question currentQuestion = questionList.get(currentQuestionIndex);
        Answer answer = null;

        switch (currentQuestion.getType()) {
            case "text":
                String text = currentEditText.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(this, "Please enter an answer", Toast.LENGTH_SHORT).show();
                    return false;
                }
                
                // Validate mobile number - must be exactly 10 digits
                String questionText = currentQuestion.getQuestionText().toLowerCase();
                if (questionText.contains("mobile") || questionText.contains("phone") || 
                    questionText.contains("contact") || (questionText.contains("number") && 
                    (questionText.contains("mobile") || questionText.contains("phone") || 
                     questionText.contains("contact")))) {
                    // Check if it's exactly 10 digits
                    if (text.length() != 10) {
                        Toast.makeText(this, "Mobile number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    // Double check all characters are digits
                    for (char c : text.toCharArray()) {
                        if (!Character.isDigit(c)) {
                            Toast.makeText(this, "Mobile number must contain only digits", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                }
                
                answer = new Answer(currentQuestion.getId(), text);
                break;

            case "single":
                int selectedId = currentRadioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
                    return false;
                }
                answer = new Answer(currentQuestion.getId(), selectedId);
                break;

            case "multiple":
                List<Integer> selectedIds = new ArrayList<>();
                for (CheckBox checkBox : currentCheckBoxes) {
                    if (checkBox.isChecked()) {
                        selectedIds.add(checkBox.getId());
                    }
                }
                if (selectedIds.isEmpty()) {
                    Toast.makeText(this, "Please select at least one option", Toast.LENGTH_SHORT).show();
                    return false;
                }
                answer = new Answer(currentQuestion.getId(), selectedIds);
                break;
        }

        // Add or update answer in list
        if (answer != null) {
            // Remove existing answer for this question if any
            for (int i = 0; i < answerList.size(); i++) {
                if (answerList.get(i).getQuestionId() == currentQuestion.getId()) {
                    answerList.remove(i);
                    break;
                }
            }
            answerList.add(answer);
            return true;
        }

        return false;
    }

    /**
     * Handle Submit button click
     */
    private void handleSubmit() {
        if (!saveCurrentAnswer()) {
            return;
        }

        // Validate that all questions are answered
        if (answerList.size() < questionList.size()) {
            int unanswered = questionList.size() - answerList.size();
            new AlertDialog.Builder(this)
                    .setTitle("Incomplete Survey")
                    .setMessage("You have " + unanswered + " unanswered question(s). Please answer all questions before submitting.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Confirm submission
        new AlertDialog.Builder(this)
                .setTitle("Submit Survey")
                .setMessage("Are you sure you want to submit your responses?")
                .setPositiveButton("Submit", (dialog, which) -> submitSurvey())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Submit survey responses to server
     */
    private void submitSurvey() {
        showLoading(true);

        try {
            // Build JSON payload
            JSONObject payload = new JSONObject();
            payload.put("user_id", userId);
            payload.put("survey_id", SURVEY_ID);
            payload.put("area_id", areaId);
            payload.put("ward_id", wardId);

            // Build answers array
            JSONArray answersArray = new JSONArray();
            for (Answer answer : answerList) {
                JSONObject answerJson = new JSONObject();
                answerJson.put("question_id", answer.getQuestionId());

                // For text answers - explicitly set answer_text and null for selected_option_id
                if (answer.getAnswerText() != null && !answer.getAnswerText().trim().isEmpty()) {
                    answerJson.put("answer_text", answer.getAnswerText().trim());
                    // Don't include selected_option_id at all for text answers
                }
                // For single choice answers - explicitly set selected_option_id and null for answer_text
                else if (answer.getSelectedOptionId() != null) {
                    answerJson.put("selected_option_id", answer.getSelectedOptionId());
                    // Don't include answer_text at all for option answers
                }
                // For multiple choice answers
                else if (answer.getSelectedOptionIds() != null && !answer.getSelectedOptionIds().isEmpty()) {
                    JSONArray idsArray = new JSONArray();
                    for (int id : answer.getSelectedOptionIds()) {
                        idsArray.put(id);
                    }
                    answerJson.put("selected_option_ids", idsArray);
                    // Don't include answer_text or selected_option_id for multiple choice
                }
                // Fallback - should not happen, but log it
                else {
                    System.err.println("Warning: Answer for question " + answer.getQuestionId() + " has no valid data");
                    continue; // Skip this answer
                }

                answersArray.put(answerJson);
            }
            
            // Log the payload for debugging
            System.out.println("Submitting survey with " + answersArray.length() + " answers");
            System.out.println("Payload JSON: " + payload.toString());
            
            // Log each answer for debugging
            for (int i = 0; i < answersArray.length(); i++) {
                try {
                    JSONObject ans = answersArray.getJSONObject(i);
                    System.out.println("Answer " + (i + 1) + ": question_id=" + ans.optInt("question_id") + 
                                     ", answer_text=" + ans.optString("answer_text", "NOT_SET") + 
                                     ", selected_option_id=" + ans.optInt("selected_option_id", -999) + 
                                     ", selected_option_ids=" + (ans.has("selected_option_ids") ? ans.getJSONArray("selected_option_ids").toString() : "NOT_SET"));
                } catch (Exception e) {
                    System.err.println("Error logging answer " + i + ": " + e.getMessage());
                }
            }
            payload.put("answers", answersArray);

            // Send POST request
            ApiService.post(ApiService.SAVE_RESPONSES, payload, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(SurveyActivity.this,
                                "Unable to connect to server",
                                Toast.LENGTH_LONG).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseBody = response.body().string();

                    runOnUiThread(() -> {
                        showLoading(false);
                        handleSubmitResponse(responseBody);
                    });
                }
            });

        } catch (JSONException e) {
            showLoading(false);
            Toast.makeText(this, "Error creating submission", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle submit response from server
     */
    private void handleSubmitResponse(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            boolean success = jsonResponse.getBoolean("success");

            if (success) {
                // Get area data from SharedPreferences
                SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                int areaId = prefs.getInt("area_id", -1);
                String areaName = prefs.getString("area_name", "");
                
                // Navigate to Ward Selection Activity
                Intent intent = new Intent(SurveyActivity.this, WardSelectionActivity.class);
                intent.putExtra("area_id", areaId);
                intent.putExtra("area_name", areaName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                String message = jsonResponse.optString("message", "Failed to submit survey");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show/hide loading state
     */
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnNext.setEnabled(!isLoading);
        btnSubmit.setEnabled(!isLoading);
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
            new AlertDialog.Builder(this)
                    .setTitle("Exit Survey")
                    .setMessage("Are you sure you want to exit? Your progress will be lost.")
                    .setPositiveButton("Exit", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Stay", null)
                    .show();
        }
    }
}
