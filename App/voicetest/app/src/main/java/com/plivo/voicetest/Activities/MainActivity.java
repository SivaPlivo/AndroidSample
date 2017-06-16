package com.plivo.voicetest.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.voicetest.Helpers.EndPointListner;
import com.plivo.voicetest.Helpers.HttpUtils;
import com.plivo.voicetest.Helpers.OnBackPressedListener;
import com.plivo.voicetest.Helpers.Phone;
import com.plivo.voicetest.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements EndPointListner, GoogleApiClient.OnConnectionFailedListener {

    public Endpoint endpoint;

    public Incoming incomingCall;

    private GoogleApiClient mGoogleApiClient;

    private static int RC_SIGN_IN = 100;

    SharedPreferences sharedPreferences;

    protected OnBackPressedListener onBackPressedListener;

    EditText username,password;
    Button loginBtn;

    String usernameStr, passwordStr;

    private static int FIRST_ELEMENT = 0;

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.requestPermissions();

        progress = new ProgressDialog(MainActivity.this);

        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        endpoint = Phone.getInstance(this).endpoint;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameStr = sharedPreferences.getString("username","");

        if (usernameStr.length() > 0) {

            passwordStr = sharedPreferences.getString("password","");

            this.showProgressView();

            endpoint.login(usernameStr, passwordStr);

        }else {


        }

    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();
    }

    public void login(View view) {

        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {

            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();

        } else {

            Log.d("PlivoInbound", "Trying to log in");

            this.showProgressView();
            usernameStr = username.getText().toString();
            passwordStr = password.getText().toString();
            endpoint.login(usernameStr, passwordStr);
        }
    }

    public void showProgressView()
    {
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    public void loginWithGoogle(View view){

        showProgressView();

        signIn();

    }

    private void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void onLogin() {

        Log.d("PlivoInbound", "Logging in");

        MainActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                // To dismiss the dialog
                progress.dismiss();

                sharedPreferences.edit().putString("username", usernameStr).apply();
                sharedPreferences.edit().putString("password", passwordStr).apply();

            }
        });

        Log.d("Listner","Phone");

        Intent intent = new Intent(this, TabsContainer.class);
        startActivity(intent);

    }

    public void onLogout() {

        Log.d("PlivoInbound", "Logged out");

    }

    public void onLoginFailed() {

        Log.d("PlivoInbound", "Login failed");

    }

    public void onIncomingCall(Incoming incoming) {

    }

    public void onIncomingCallHangup(Incoming incoming) {

    }

    public void onIncomingCallRejected(Incoming incoming) {

    }

    public void onOutgoingCall(Outgoing outgoing) {

    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {

    }

    public void onOutgoingCallHangup(Outgoing outgoing) {

    }

    public void onOutgoingCallInvalid(Outgoing outgoing){

    }

    public void onIncomingDigitNotification(String digits) {

    }

    public  void  requestPermissions()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.PROCESS_OUTGOING_CALLS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_SETTINGS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WAKE_LOCK},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.VIBRATE},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_LOGS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_LOGS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.USE_SIP)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.USE_SIP},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google SignIn", "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();

            String email = acct.getEmail();
            String username = acct.getDisplayName();

            RequestParams rp = new RequestParams();

            String urlString = "https://iossdk:0d0c2ea141d0b7cde33cc3cef9@plivojs.herokuapp.com/restAPI.php?email="+email;

            Log.d("SIP Url", " response : " + urlString);

            HttpUtils.post(urlString, rp, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray sipContactsArray)
                {

                    try {

                        ArrayList<String> list = new ArrayList<String>();
                        if (sipContactsArray != null) {
                            int len = sipContactsArray.length();

                            for (int i=0; i<len; i++){

                                list.add(sipContactsArray.get(i).toString());

                            }
                        }

                        Log.d("SIP Contacts", " response : " + list);

//                        sharedPreferences.edit().putString("username", sipContactsArray.getJSONObject(FIRST_ELEMENT).toString());
//                        sharedPreferences.edit().putString("password", "7bjbk5ib7f23ie05be2h5713hf1hl774j94c88244e2185b").apply();

                        Log.d("SIP", " Username " + list.get(0));

                        //Set the values
                        Set<String> set = new HashSet<String>();
                        set.addAll(list);
                        sharedPreferences.edit().putStringSet("SIP Contacts", set).apply();
                        sharedPreferences.edit().commit();

                        //Retrieve the values
                        Set<String> set2 = sharedPreferences.getStringSet("SIP Contacts", null);
                        Log.d("SIP", " Set " + set2);

                        usernameStr = "siva170404101012";
                        passwordStr = "7bjbk5ib7f23ie05be2h5713hf1hl774j94c88244e2185b";

                        endpoint.login(usernameStr, passwordStr);

                    } catch (JSONException e) {
                        // Do something with the exception
                        Log.d("Exception",e.getLocalizedMessage());
                    }
                }
            });


        } else {
            // Signed out, show unauthenticated UI.
            Log.d("Google SignIn", "Signed out");

        }
    }

    class MyTypeAdapter<T> extends TypeAdapter<T> {
        public T read(JsonReader reader) throws IOException {
            return null;
        }

        public void write(JsonWriter writer, T obj) throws IOException {
            if (obj == null) {
                writer.nullValue();
                return;
            }
            writer.value(obj.toString());
        }

    }

}
//        ArrayList<RecentCall> recentCallsArrayList = new ArrayList<RecentCall>();
//
//        for(int i=0; i< 10;i++)
//        {
//            RecentCall recentCall = new RecentCall();
//            recentCall.setPhone("918686198299");
//            recentCall.setTime("4 hrs. ago");
//            recentCallsArrayList.add(recentCall);
//        }
//
//        SharedPreferences db = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//
//        SharedPreferences.Editor collection = db.edit();
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(RecentCall.class, new MyTypeAdapter<RecentCall>())
//                .create();
//        if (gson != null)
//        {
//            String arrayList1 = gson.toJson(recentCallsArrayList);
//            collection.putString("RecentCallsInfo", arrayList1);
//            collection.commit();
//        }
//        else
//        {
//            Log.d("gson","nil");
//        }