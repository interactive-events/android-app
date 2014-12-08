package com.wordpress.interactiveevents.interactive_events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world", "a@a.a:asdfg"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mainBtn;
    private Button urlBtn;
    private Button beaconBtn;
    private Button pushBtn;
    final Context context = this;

    static String API = "http://private-274c2-interactiveevents.apiary-mock.com/";
    //getBeaconEvent getBeaconEvent;
    TableLayout eventsTable;
    String beaconID;
    Integer beaconMajor,beaconMinor;

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Storage.initSharedPrefs(context);
        if (Storage.accessTokenValid()) {
            // skip login-screen and show event-list-home-screen
            Intent eventList = new Intent(getApplicationContext(), EventListActivity.class);
            //eventList.putExtra("someVariable", "someValue");
            startActivity(eventList);

            finish(); // remove this activity from stack, this activity is never shown to the user
            // if access_token is still valid
            // this prevents being able to press the backwards key and see an empty screen

        } else {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            iv = new ImageView(context);
            iv = (ImageView) findViewById(R.id.imageView);

            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
            populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);

            //KNAPPTEST
            /*
            mainBtn = (Button) findViewById(R.id.button);
            mainBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    openAlert(v);
                }
            });
            */

            //WEBVIEWTEST
            /*
            urlBtn = (Button) findViewById(R.id.buttonUrl);

            urlBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    startActivity(intent);
                }

            });
            */
            //WEBVIEWTEST SLUT

            //KNAPPTESTBEACONS
/*            beaconBtn = (Button) findViewById(R.id.beaconButton);
            beaconBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //beaconTable(beaconID, beaconMajor, beaconMinor);
                    Intent eventList = new Intent(getApplicationContext(), EventListActivity.class);

                    //beaconService.putExtra("beacon_ID", "1");
                    //beaconService.putExtra("beacon_major", 1);
                    //beaconService.putExtra("beacon_minor", 1);
                    //Log.i("#########dafuq#########", "gg?");

                    startActivity(eventList);
                }
            });
            */

            /*
            //KNAPPTESTBEACONS
            pushBtn = (Button) findViewById(R.id.pushButton);
            pushBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //beaconTable(beaconID, beaconMajor, beaconMinor);
                    Intent pushApp = new Intent(getApplicationContext(), AndroidMobilePushApp.class);

/*
                beaconService.putExtra("beacon_ID", "1");
                beaconService.putExtra("beacon_major", 1);
                beaconService.putExtra("beacon_minor", 1);

                Log.i("#########dafuq#########", "gg?");
                    startActivity(pushApp);
                }
            });
            */
        }

    }
    /*
    public void beaconTable(String beaconID, Integer beaconMajor, Integer beaconMinor) {
        //showProgress(true);

        getBeaconEvent = new getBeaconEvent(beaconID, beaconMajor, beaconMinor);
        getBeaconEvent.execute((Void) null);
    }
    */
    //KNAPPTEST

    private void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

        alertDialogBuilder.setTitle(this.getTitle()+ " decision");
        alertDialogBuilder.setMessage("Nearby event attended! \n Name : [Hårdkodad]");
        // set positive button: Yes message
        alertDialogBuilder.setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // go to a new activity of the app
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "You are now attending: [Hårdkodad]",
                        Toast.LENGTH_LONG).show();

            }
        });
        // set negative button: No message
        alertDialogBuilder.setPositiveButton("Take me to event!",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // cancel the alert box and put a Toast to the user
                Intent positveActivity = new Intent(getApplicationContext(),
                        EventActivity.class);
                startActivity(positveActivity);
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    //KNAPPTEST SLUT



    //KNAPPTEST2

    private void openBeaconAlert(final String eventName,final String beacID, final String eventDesc) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

        alertDialogBuilder.setTitle(this.getTitle()+ " decision");
        alertDialogBuilder.setMessage("Beacon detected! \n ID :"+beacID+"\n Event name : "+eventName);
        // set positive button: Yes message
        alertDialogBuilder.setNegativeButton("Ignore.",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

                dialog.cancel();
                Toast.makeText(getApplicationContext(), "Ignored event: "+eventName,
                        Toast.LENGTH_LONG).show();

            }
        });
        // set negative button: No message
        alertDialogBuilder.setPositiveButton("Take me to event!",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // cancel the alert box and put a Toast to the user
                Intent positveActivity = new Intent(getApplicationContext(), EventActivity.class);
                positveActivity.putExtra("event_title", eventName);
                positveActivity.putExtra("event_desc", eventDesc);
                startActivity(positveActivity);

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    //KNAPPTEST2 SLUT



    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //cancel = true; // TODO: change this..
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true; //email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //return password.length() > 4;
        return true;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            /*
            mainBtn.setVisibility(show ? View.VISIBLE : View.GONE);
            mainBtn.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mainBtn.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            */


        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

        addEmailsToAutoComplete(emails);
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


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // do a POST http(s) request, with
            // grant_type, client_id, client_secret, username, password
            // to http://interactive-events.elasticbeanstalk.com/oauth/token
            // save token obj in some persistent storage...

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://interactive-events.elasticbeanstalk.com/oauth/token");

            // POST parameters
 /*           List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
            nameValuePair.add(new BasicNameValuePair("grant_type", "password"));
            nameValuePair.add(new BasicNameValuePair("client_id", "1"));
            nameValuePair.add(new BasicNameValuePair("client_secret", "secret"));
            nameValuePair.add(new BasicNameValuePair("username", mEmail));
            nameValuePair.add(new BasicNameValuePair("password", mPassword));
            Log.d("login", "nameValuePair="+nameValuePair.toString());
            // Encode POST data into valid URL format before making HTTP request

            UrlEncodedFormEntity entity = null;
            try {
                entity = new UrlEncodedFormEntity(nameValuePair);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            entity.setContentType("application/form-data");
            httpPost.setEntity(entity);
            httpPost.setHeader("Content-Type", "multipart/form-data;boundary=,");*/

            StringEntity data = null;
            try {
                String d = String.format("{\"grant_type\":\"password\",\"client_id\":\"546df0e1509295c52832bee9\",\"client_secret\":\"thiIsForTheAndroidApp\",\"username\":\"%s\",\"password\":\"%s\"}", mEmail, mPassword);
                data = new StringEntity(d);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //httpPost.setHeader("Accept", "application/json");

            data.setContentType("application/json; charset=UTF-8"); //; charset=UTF-8
            //data.setContentEncoding("application/json; charset=UTF-8");
            httpPost.setEntity(data);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");

            // Making the HTTP POST request
            String result = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                String responseStatus = String.valueOf(response.getStatusLine());
                Log.d("login", "response.getStatusLine="+responseStatus);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }
                result = builder.toString();
                if (result != null) {
                    //Log.d("login", "result="+result);
                } else {
                    Log.d("login", "Login http POST request failed");
                    return false;
                }
                if (!responseStatus.equals("HTTP/1.1 200 OK")) {
                    return false;
                }
            } catch (ClientProtocolException e) {
                // Log exception
                e.printStackTrace();
            } catch (IOException e) {
                // Log exception
                e.printStackTrace();
            }
            JSONObject jObj = null;
            try {
                jObj = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("login", "jObj="+jObj);
            String access_token = null;
            String refresh_token = null;
            String user_id = null;
            long expires_in = 0;
            try {
                access_token = (String) jObj.get("access_token");
                Log.d("login", "Got access_token="+access_token);
                refresh_token = (String) jObj.get("refresh_token");
                user_id = (String) jObj.get("user_id");
                expires_in = jObj.getLong("expires_in");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // TODO: save tokens in persistent storage

         /*   SharedPreferences auth_prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

            // Need an Editor object to make preference changes
            // All objects are from android.context.Context?
            SharedPreferences.Editor editor = auth_prefs.edit();
            editor.putString("access_token", access_token);
            // Commit the edits!
            editor.commit();

            Log.d("login", "fromSharedPrefs, access_token="+auth_prefs.getString("access_token", "No token stored"));
*/
            //Log.d("login", "before Storage.setNewAccessToken(access_token) call");
            Storage.setNewAccessToken(access_token);
            Storage.setExpireDateForNewestToken(expires_in);
            Storage.setUserId(user_id);
            //Log.d("login", "after Storage.setNewAccessToken(access_token) call");
            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), EventListActivity.class);
                Intent pushApp = new Intent(getApplicationContext(), MessageReceivingService.class);

                startService(pushApp);
                startActivity(nextScreen);

                finish(); // prevents going back to login-screen when successfully logged in
                overridePendingTransition(R.anim.anim_slide_in_up, R.anim.anim_slide_out_up);

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }




    //TESTAR [BEACON sens]
    /*
    public class getBeaconEvent extends AsyncTask<Void, Void, String> {
        Integer beaconMajor,beaconMinor;
        String beaconID;

        getBeaconEvent(String beaconID, Integer beaconMajor, Integer beaconMinor) {
            this.beaconID = beaconID;
            this.beaconMajor = beaconMajor;
            this.beaconMinor = beaconMinor;
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.i("API", "Starting get api stuff");

            InputStream inputStream = null;

            String access_token = Storage.getAccessToken();
            String access_param = "?access_token="+access_token;

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(API+"events"+access_param+"&beaconsUUID="+beaconID+"&beaconsMinor="+beaconMinor+"&beaconsMajor="+beaconMajor);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                Log.i("API", "server returned status code "+httpResponse.getStatusLine());

                //random debugging
                Log.i("BEACONREQUEST", "request to server: "+httpGet.getRequestLine());
                Log.i("TESTARRANDOMSHIT", "beaconID="+beaconID);
                Log.i("TESTARRANDOMSHIT2", "beaconsMinor="+beaconMinor);
                Log.i("TESTARRANDOMSHIT3", "beaconsMajor="+beaconMajor);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String result = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try{
                    if(inputStream != null) {
                        inputStream.close();
                    }
                }catch(Exception squish){

                }
            }

            if(result != null) {
                Log.i("API", result);
            } else {
                Log.e("API", "failed server connections or something!");
                return null;
            }
            return result;
        }


        @Override
        protected void onPostExecute(final String result) {
            getBeaconEvent = null;

            if (result == null) {
                //eventsTable.setError(getString(R.string.error_connection_problem));
                eventsTable.requestFocus();
                return;
            }

            JSONObject jObj;
            JSONArray beacons;
            String totalCount;
            try {
                jObj = new JSONObject(result);
                beacons = jObj.getJSONArray("events");
                JSONObject metaData = jObj.getJSONArray("_metadata").getJSONObject(0);
                totalCount = metaData.getString("totalCount");
                for (int i=0; i < 1; i++)
                {
                    JSONObject event = beacons.getJSONObject(i).getJSONObject("event");
                    final String event_title = event.getString("title");
                    final String beacon_id = event.getString("beaconId");
                    final String event_desc = event.getString("description");
                    Log.i("API", event_title+", "+beacon_id+", "+event_desc);

                    //call popup method, populate with actual beacon data

                    //openBeaconAlert(event_title,beacon_id,event_desc);

                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                return;
            }

            Log.i("API", "totalCount: "+totalCount);

        }

        @Override
        protected void onCancelled() {
            getBeaconEvent = null;
            //showProgress(false);
        }
    }
    */
    //TESTAR [BEACON sens]

    /* REGISTER USER, SKA DOCK EJ IMPLEMENTERAS NU */
    public void createUser(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

}
