package za.co.exampleapp.masilibalestoto.exampleapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import za.co.exampleapp.masilibalestoto.exampleapp.helper.AppController;
import za.co.exampleapp.masilibalestoto.exampleapp.helper.Config_URL;
import za.co.exampleapp.masilibalestoto.exampleapp.helper.SQLiteHandler;
import za.co.exampleapp.masilibalestoto.exampleapp.helper.SessionManager;

public class Activity_UploadValues extends Activity
{
    // LogCat tag
    private static final String TAG = Activity_Register.class.getSimpleName();
    private Button btnUploadValues;
    private Button btnLogout;
    private SeekBar value1;
    private EditText value2;
    private ProgressDialog pDialog;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_values);

        value1 = (SeekBar) findViewById(R.id.value1);
        value2 = (EditText) findViewById(R.id.value2);
        btnUploadValues = (Button) findViewById(R.id.btnUploadValues);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
//        if (session.isLoggedIn()) {
//            // User is already logged in. Take him to main activity
//            Intent intent = new Intent(Activity_UploadValues.this, .class);
//            startActivity(intent);
//            finish();
//        }

        // Login button Click Event
        btnUploadValues.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                HashMap<String, String> user = db.getUserDetails();

                String email = user.get("email");
                int _value1 = value1.getProgress();
                String _value2 = value2.getText().toString();
                // Check for empty data in the form
                if (_value1 > 0 && _value2.trim().length() > 0) {
                    uploadValuesToServer(email, String.valueOf(_value1), _value2);
                } else {
                    // Prompt user to provide us values
                    Toast.makeText(getApplicationContext(),
                            "Please provide both values!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Logout Screen
        btnLogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                logoutUser();
            }
        });

    }

    /**
     * function to upload values to server
     * */
    private void uploadValuesToServer(final String email, final String firstValue, final String secondValue) {
        // Tag used to cancel the request
        String tag_string_req = "req_upload_values";

        pDialog.setMessage("Uploading...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config_URL.URL_UPLOAD_VALUES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Uploading Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // Success, reset data input end point
                        resetValueInput();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    Toast.makeText(getApplicationContext(),
                            "Server error occured. Try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Upload Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to upload values url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "uploadValues");
                params.put("email", email);
                params.put("value1", firstValue);
                params.put("value2", secondValue);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(Activity_UploadValues.this, Activity_Login.class);
        startActivity(intent);
        finish();
    }

    private void resetValueInput(){
        value1.setProgress(0);
        value2.setText("");
        value1.requestFocus();
    }

}