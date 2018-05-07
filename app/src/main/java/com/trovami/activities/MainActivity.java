package com.trovami.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.trovami.R;
import com.trovami.databinding.ActivityMainBinding;
import com.trovami.utils.Utils;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_GOOGLE_SIGN_IN = 999;
    private ActivityMainBinding mBinding;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mFacebookCallbackManagaer;
    private FirebaseAuth mAuth;
    private AuthCredential fbCredential;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupFirebaseAuth();
        if(isLoggedId()) {
            loginUser();
            return;
        }
        this.setupUI();
        this.setupGoogleClient();
        this.setupFacebookClient();
    }

    private void setupUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mBinding.toolbar);
        mBinding.signInGoogle.setOnClickListener(this);
        //mBinding.signInFacebook.setOnClickListener(this);
        mBinding.signInEmail.setOnClickListener(this);
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Logging in...");
        mDialog.setCancelable(false);
    }

    private void setupFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setupGoogleClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();
    }

    private void setupFacebookClient() {
        fbCredential = null;
        mFacebookCallbackManagaer = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        mBinding.loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        mBinding.loginButton.registerCallback(mFacebookCallbackManagaer, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                Log.d(TAG, "facebook:onSuccess:" + loginResult.getAccessToken());
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "facebook:onError", exception);
            }
        });
    }

    private boolean isLoggedId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_google:
                this.signinGoogle();
                break;
            /*case R.id.sign_in_facebook:
                // TODO: handle fb login
                break; */
            case R.id.sign_in_email:
                this.startEmailSigninActivity();
                break;
        }
    }

    private void startEmailSigninActivity() {
        Intent intent = new Intent(this, EmailSigninActivity.class);
        startActivity(intent);
    }

    private void startMapFragment() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void signinGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManagaer.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.d(TAG, account.getDisplayName());
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                // TODO: handle error
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        mDialog.show();
        final Activity mainActivity = this;
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        loginUser();
                        if (fbCredential != null) {
                            mAuth.getCurrentUser().linkWithCredential(fbCredential);
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Utils.safeToast(getBaseContext(), task.getException().getLocalizedMessage());
                    }
                }
            }
        );
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        mDialog.show();
        final Activity mainActivity = this;
        fbCredential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(fbCredential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.dismiss();
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        loginUser();
                        fbCredential = null;
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if(task.getException().getClass().equals(FirebaseAuthUserCollisionException.class)){
                            Utils.safeToast(getBaseContext(), "Already logged in with Google. Please login with google (without logging out of FB)to link accounts.");
                        } else {
                            Utils.safeToast(getBaseContext(), task.getException().getLocalizedMessage());
                            fbCredential = null;
                        }
                    }
                }
            }
        );
    }

    private void loginUser() {
        Intent loginIntent = new Intent(this, DashboardActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
