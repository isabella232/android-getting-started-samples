package com.adobe.userauthui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.adobe.creativesdk.foundation.auth.AdobeAuthException;
import com.adobe.creativesdk.foundation.auth.AdobeAuthSessionHelper;
import com.adobe.creativesdk.foundation.auth.AdobeAuthSessionLauncher;
import com.adobe.creativesdk.foundation.auth.AdobeUXAuthManager;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    static final int REQ_CODE_CSDK_USER_AUTH = 1001;

    /* 1 */
    private AdobeUXAuthManager mUXAuthManager = AdobeUXAuthManager.getSharedAuthManager();
    private AdobeAuthSessionHelper mAuthSessionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* 2 */
        mAuthSessionHelper = new AdobeAuthSessionHelper(mStatusCallback);
        mAuthSessionHelper.onCreate(savedInstanceState);

    }

    private AdobeAuthSessionHelper.IAdobeAuthStatusCallback mStatusCallback;
    {
        mStatusCallback = new AdobeAuthSessionHelper.IAdobeAuthStatusCallback() {
            @Override
            public void call(AdobeAuthSessionHelper.AdobeAuthStatus adobeAuthStatus, AdobeAuthException e) {
                if (!mUXAuthManager.isAuthenticated()) {
                    /* 3 */
                    login();
                } else {
                    Log.d(TAG, "Already logged in!");
                }
            }
        };
    }

    /* 4 */
    private void login() {
        final String[] authScope = {"email", "profile", "address"};

        AdobeAuthSessionLauncher authSessionLauncher = new AdobeAuthSessionLauncher.Builder()
                .withActivity(this)
                .withRedirectURI(Keys.CSDK_REDIRECT_URI)
                .withAdditonalScopes(authScope)
                .withRequestCode(REQ_CODE_CSDK_USER_AUTH) // Can be any int
                .build();

        mUXAuthManager.login(authSessionLauncher);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuthSessionHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuthSessionHelper.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuthSessionHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuthSessionHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthSessionHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAuthSessionHelper.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_CSDK_USER_AUTH:
                    Log.i(TAG, "User successfully logged in!");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mUXAuthManager.logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
