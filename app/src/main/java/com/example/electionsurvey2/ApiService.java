package com.example.electionsurvey2;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * API Service - Utility class for making HTTP requests
 * Provides reusable methods for GET and POST requests
 */
public class ApiService {

    // Base URL - Remote server IP address
    public static final String BASE_URL = "http://143.110.252.32:4000";


    // API Endpoints
    public static final String LOGIN = "/api/login";
    public static final String AREAS = "/api/areas";
    public static final String WARDS = "/api/wards/"; // + areaId
    public static final String SURVEY_QUESTIONS = "/api/surveys/"; // + surveyId + /questions
    public static final String SAVE_RESPONSES = "/api/responses";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client;

    /**
     * Get singleton OkHttpClient instance
     */
    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }

    /**
     * Make GET request
     * @param endpoint API endpoint path
     * @param callback Response callback
     */
    public static void get(String endpoint, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .get()
                .build();

        getClient().newCall(request).enqueue(callback);
    }

    /**
     * Make POST request with JSON body
     * @param endpoint API endpoint path
     * @param jsonBody JSON request body
     * @param callback Response callback
     */
    public static void post(String endpoint, JSONObject jsonBody, Callback callback) {
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body)
                .build();

        getClient().newCall(request).enqueue(callback);
    }

    /**
     * Parse successful response
     * @param response OkHttp response
     * @return JSONObject or null if error
     */
    public static JSONObject parseResponse(Response response) {
        try {
            String responseBody = response.body().string();
            return new JSONObject(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Response callback interface for simplified handling
     */
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    /**
     * Wrapper method with simplified callback
     */
    public static void request(String endpoint, JSONObject body, boolean isPost, ApiCallback apiCallback) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                apiCallback.onError("Unable to connect to server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonResponse = parseResponse(response);
                    if (jsonResponse != null) {
                        apiCallback.onSuccess(jsonResponse);
                    } else {
                        apiCallback.onError("Invalid response from server");
                    }
                } else {
                    apiCallback.onError("Server error: " + response.code());
                }
            }
        };

        if (isPost && body != null) {
            post(endpoint, body, callback);
        } else {
            get(endpoint, callback);
        }
    }
}



