package com.wordpress.interactiveevents.interactive_events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Webbpiraten on 2014-12-03.
 */
public class RegisterActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world", "a@a.a:asdfg"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask rAuthTask = null;

    // UI references.
    private AutoCompleteTextView rEmailView;
    private EditText rPasswordView;
    private EditText rNameView;
    private View rProgressView;
    private View RegisterFormView;
    private Button regBtn;
    final Context context = this;

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iv = new ImageView(context);
        iv = (ImageView) findViewById(R.id.imageView);

        rNameView =(EditText) findViewById(R.id.name);

        // Set up the login form.
        rEmailView = (AutoCompleteTextView) findViewById(R.id.email_reg);
        populateAutoComplete();

        rPasswordView = (EditText) findViewById(R.id.password_reg);
        rPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });


        Button rEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        rEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
        RegisterFormView = findViewById(R.id.register_form);
        rProgressView = findViewById(R.id.register_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegister() {
        // Reset errors.
        rNameView.setError(null);
        rEmailView.setError(null);
        rPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = rNameView.getText().toString();
        String email = rEmailView.getText().toString();
        String password = rPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check that a name has been given.
        if (!TextUtils.isEmpty(name) && !isNameValid(name)){
            rNameView.setError(getString(R.string.error_invalid_name));
            focusView = rNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            rPasswordView.setError(getString(R.string.error_invalid_password_reg));
            focusView = rPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            rEmailView.setError(getString(R.string.error_field_required_reg));
            focusView = rEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            rEmailView.setError(getString(R.string.error_invalid_email_reg));
            focusView = rEmailView;
            cancel = true;
        }

        //cancel = true; // TODO: change this..
        if (cancel) {
            // There was an error; don't attempt create and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user creation attempt.
            //showProgress(true);
            rAuthTask = new UserRegisterTask(name, email, password);
            rAuthTask.execute((Void) null);
        }
    }

    private boolean isNameValid(String name){
        return name.length() > 0;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true; //email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            RegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            RegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    RegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            rProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            rProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            regBtn.setVisibility(show ? View.VISIBLE : View.GONE);
            regBtn.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    regBtn.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });


        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            rProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            RegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        //addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
    /**
     * Represents an asynchronous registration task used to create users.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String rName;
        private final String rEmail;
        private final String rPassword;

        UserRegisterTask(String name, String email, String password) {
            rName = name;
            rEmail = email;
            rPassword = password;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://interactive-events.elasticbeanstalk.com/users");
            StringEntity data = null;
            try {
                String d = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",rName, rEmail, rPassword);
                data = new StringEntity(d);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            data.setContentType("application/json; charset=UTF-8");
            httpPost.setEntity(data);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

            // Making the HTTP POST request
            String result = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                String responseStatus = String.valueOf(response.getStatusLine());
                Log.d("register", "response.getStatusLine=" + responseStatus);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                result = builder.toString();
                if (result != null) {
                    //Log.d("login", "result="+result);
                } else {
                    Log.d("create", "Create http POST request failed");
                    return false;
                }
                if (!responseStatus.equals("HTTP/1.1 201 Created")) {
                    return false;
                }
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            //showProgress(false);
            if (success) {
                //Starting a new Intent
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                overridePendingTransition(R.anim.leftanim, R.anim.rightanim);
                startActivity(intent);
                finish(); // prevents going back to register-screen when successfully creating a user
            } else {
                rEmailView.setError(getString(R.string.error_invalid_email_reg));
                rEmailView.requestFocus();
            }
        }
        @Override
        protected void onCancelled() {
            rAuthTask = null;
            showProgress(false);
        }
    }

}
