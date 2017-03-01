package com.callcenter.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

            myTimer.schedule(new TimerTask() {

                @Override
                public void run() {

                    if (FirebaseInstanceId.getInstance().getToken() != null) {

                        login = inputLogin.getText().toString().trim();
                        password = inputPassword.getText().toString().trim();

                        (new Task()).execute();

                        myTimer.cancel();
                    }
                }
            }, 0, 1000);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateLogin() {
        if (inputLogin.getText().toString().trim().isEmpty()) {
            errorLogin();
            return false;
        } else {
            inputLayoutLogin.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            errorPassword();
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void errorLogin() {
        inputLayoutLogin.setError(getString(R.string.err_msg_login));
        requestFocus(inputLogin);
    }

    private void errorPassword() {
        inputLayoutPassword.setError(getString(R.string.err_msg_password));
        requestFocus(inputPassword);
    }

    private void requestFocus(final View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    class Task extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(final Void... voids) {

            final HttpClient httpClient = new HttpClient();
            String response = "";
            try {
//                final String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                final Map<String, String> header = new HashMap<>();
                header.put("Accept", "application/json");
                header.put("Content-Type", "application/x-www-form-urlencoded");
                header.put("clientId", "3");
//              header.put("pushToken", refreshedToken);

                final String body = "grant_type=password&username=" + login + "&password=" + password;
                response = httpClient.post(Constants.URL_REGISTRATION, header, body);

            } catch (final Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(final String response) {
            super.onPostExecute(response);

            progressBar.setVisibility(View.INVISIBLE);

            if (response != null) {
                try {
                    final JSONObject dataJsonObj = new JSONObject(response);
                    if (dataJsonObj.has("access_token")) {

                        final String access_token = dataJsonObj.getString("access_token");

                        linearLayout.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);

                        sPref.edit().putBoolean(Constants.KEY_REGISTRATION, true).apply();
                        sPref.edit().putString(Constants.KEY_AUTH_TOKEN, access_token).apply();

                    } else if (dataJsonObj.has("error")) {
                        errorPassword();
                    } else {
                        errorLogin();
                    }

                } catch (final JSONException e) {
                    errorLogin();

                    e.printStackTrace();
                }

            } else {

                Toast.makeText(getApplicationContext(), R.string.failed_to_register, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(final String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private boolean isNetworkAvailable() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
