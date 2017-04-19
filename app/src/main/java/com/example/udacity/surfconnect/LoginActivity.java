package com.example.udacity.surfconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;

public class LoginActivity extends AppCompatActivity {

    private static final int APP_REQUEST_CODE = 1;
    AppEventsLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));
        logger = AppEventsLogger.newLogger(this);

        //Checking an access token
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if (accessToken != null) {
            //If previously logged in, proceed to the account activity
            launchAccountActivity();
        }
    }

    public void onPhoneLogin(View view) {

        logger.logEvent("On phone login");
        onLogin(LoginType.PHONE);
    }

    public void onEmailLogin(View view) {

        logger.logEvent("On email login");
        onLogin(LoginType.EMAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_REQUEST_CODE) {
            AccountKitLoginResult loginResult =
                    data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                //Display login error
                String toastMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
            } else if (loginResult.getAccessToken() != null) {
                launchAccountActivity();
            }
        }
    }

    private void onLogin(final LoginType loginType) {
        //Crate an intent for the account kit activity
        final Intent intent = new Intent(this, AccountKitActivity.class);

        //Configure the login type and response type
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        loginType,
                        AccountKitActivity.ResponseType.TOKEN
                );

        final AccountKitConfiguration configuration = configurationBuilder.build();

        //Launch account kit activity
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        startActivityForResult(intent, APP_REQUEST_CODE);
    }


    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

}
