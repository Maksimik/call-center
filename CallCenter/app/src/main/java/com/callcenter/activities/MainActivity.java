package com.callcenter.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.callcenter.HttpClient;
import com.callcenter.R;
import com.callcenter.constants.Constants;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sPref;
    private EditText inputLogin, inputPassword;
    private TextInputLayout inputLayoutLogin, inputLayoutPassword;
    private ProgressBar progressBar;
    private String login, password;
    private LinearLayout linearLayout;
    private TextView textView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sPref = getSharedPreferences(Constants.PREF, MODE_PRIVATE);

        inputLogin = (EditText) findViewById(R.id.inputLogin);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputLayoutLogin = (TextInputLayout) findViewById(R.id.inputLayoutLogin);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        linearLayout = (LinearLayout) findViewById(R.id.linerLayout);
        textView = (TextView) findViewById(R.id.textView);

        final boolean registration = sPref.getBoolean(Constants.KEY_REGISTRATION, false);
        if (registration) {

            linearLayout.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            final int color = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void onClick(final View view) {

        if (!validateLogin()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        if (isNetworkAvailable()) {
            final Timer myTimer = new Timer();
            progressBar.setVisibility(View.VISIBLE);

            OneSignal.startInit(getApplicationContext()).init();
            final Handler uiHandler = new Handler();
            myTimer.schedule(new TimerTask() {

                boolean flag = true;

                @Override
                public void run() {
                    if (!isNetworkAvailable() && flag) {
                        uiHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), R.string.lost_internet_connection, Toast.LENGTH_SHORT).show();
                            }
                        });

                        flag = false;
                    } else if (isNetworkAvailable() && !flag) {
                        flag = true;
                        uiHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), R.string.internet_connection_is_restored, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {

                        @Override
                        public void idsAvailable(final String userId, final String registrationId) {

                            if (userId != null) {

                                login = inputLogin.getText().toString().trim();
                                password = inputPassword.getText().toString().trim();

                                (new Task(userId)).execute();

                                myTimer.cancel();
                            }
                        }
                    });
                }
            }, 0, 1000);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateLogin() {
        if (inputLogin.getText().toString().trim().isEmpty()) {
            errorLogin(getString(R.string.err_msg_login));
            return false;
        } else {
            inputLayoutLogin.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            errorPassword(getString(R.string.err_msg_password));
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void errorLogin(final String message) {
        inputLayoutLogin.setError(message);
        requestFocus(inputLogin);
    }

    private void errorPassword(final String message) {
        inputLayoutPassword.setError(message);
        requestFocus(inputPassword);
    }

    private void requestFocus(final View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    class Task extends AsyncTask<Void, Void, String[]> {

        private final String userId;

        Task(final String userId) {
            this.userId = userId;

        }

        @Override
        protected String[] doInBackground(final Void... voids) {

            final HttpClient httpClient = new HttpClient();
            final String[] response = {null, null};

            try {
                final OkHttpClient client = new OkHttpClient();

                final FormBody body = new FormBody.Builder()
                        .add("grant_type", "password")
                        .add("username", login)
                        .add("password", password)
                        .build();

                final Request request = new Request.Builder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("pushToken", userId)
                        .url(Constants.URL_REGISTRATION)
                        .post(body)
                        .build();
                final Response r = client.newCall(request).execute();

                response[0] = r.body().string();
                final Map<String, String> header;

                if (response[0] != null) {
                    try {
                        final JSONObject dataJsonObj = new JSONObject(response[0]);
                        if (dataJsonObj.has("access_token")) {
                            final String access_token = dataJsonObj.getString("access_token");

                            header = new HashMap<>();
                            header.put("Accept", "application/json");
                            header.put("Authorization", "Bearer " + access_token);

                            response[1] = httpClient.get(Constants.URL_USER, header);
                        }

                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (final Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(final String[] response) {
            super.onPostExecute(response);

            progressBar.setVisibility(View.INVISIBLE);

            if (response[1] != null) {
                try {
                    JSONObject dataJsonObj = new JSONObject(response[1]);
                    if (dataJsonObj.has("ClientId")) {
                        final String clientId = dataJsonObj.getString("ClientId");
                        dataJsonObj = new JSONObject(response[0]);
                        final String access_token = dataJsonObj.getString("access_token");

                        linearLayout.setVisibility(View.INVISIBLE);
                        textView.setText(R.string.registration_completed_successfully);
                        textView.setVisibility(View.VISIBLE);

                        sPref.edit().putBoolean(Constants.KEY_REGISTRATION, true).apply();
                        sPref.edit().putString(Constants.KEY_CLIENT_ID, clientId).apply();
                        sPref.edit().putString(Constants.KEY_LOGIN, login).apply();
                        sPref.edit().putString(Constants.KEY_AUTH_TOKEN, access_token).apply();

                    } else {

                        Toast.makeText(getApplicationContext(), R.string.failed_to_register, Toast.LENGTH_SHORT).show();
                    }
                } catch (final JSONException e) {
                    errorLogin(getString(R.string.incorrect_login));

                    e.printStackTrace();
                }

            } else if (response[0] != null) {
                try {
                    final JSONObject dataJsonObj = new JSONObject(response[0]);
                    if (dataJsonObj.has("error_description")) {

                        Toast.makeText(getApplicationContext(), dataJsonObj.getString("error_description"), Toast.LENGTH_SHORT).show();

                    }
                } catch (final JSONException e) {

                    e.printStackTrace();
                }
            } else {

                Toast.makeText(getApplicationContext(), R.string.failed_to_register, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
