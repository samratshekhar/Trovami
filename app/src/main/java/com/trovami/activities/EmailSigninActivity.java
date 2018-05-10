package com.trovami.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.trovami.R;
import com.trovami.databinding.ActivityEmailSigninBinding;
import com.trovami.utils.Utils;

public class EmailSigninActivity extends AppCompatActivity {

    private static final String TAG = "EmailSigninActivity";

    private ActivityEmailSigninBinding mBinding;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
    }

    private void setupUI() {
        setContentView(R.layout.activity_email_signin);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_email_signin);
        mBinding.signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mBinding.passwordEditText.getWindowToken(), 0);
                validateCredentials();
            }
        });
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Logging in...");
        mDialog.setCancelable(false);
    }

    private void validateCredentials() {
        String email = mBinding.emailEditText.getText().toString().trim();
        String password = mBinding.passwordEditText.getText().toString().trim();

        String emailValidation = validateEmail(email);
        String passwordValidation = validatePassword(password);

        if (emailValidation != null) {
            mBinding.emailEditTextLayout.setError(emailValidation);
        } else {
            mBinding.emailEditTextLayout.setError(null);
        }

        if (passwordValidation != null) {
            mBinding.passwordEditText.setError(passwordValidation);
        } else {
            mBinding.passwordEditText.setError(null);
        }

        if (emailValidation == null && passwordValidation == null) {
            // sign in!
            createUser(email, password);
        }
    }

    private String validateEmail(String email) {
        if (email == null || email.isEmpty()) return "Email required";
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return  "Email invalid";
        return null;
    }

    private String validatePassword(String phone) {
        if (phone == null || phone.isEmpty()) return "Password required";
        return null;
    }

    private void signIn(String email, String password) {
        mDialog.show();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Utils.safeToast(getBaseContext(), "Authentication Success");
                        sendSuccessStatusBack();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Utils.safeToast(getBaseContext(), "Authentication failed");
                    }
                }
            });

    }

    private void createUser(final String email, final String password) {
        mDialog.show();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Utils.safeToast(getBaseContext(), "Authentication Success");
                            mDialog.dismiss();
                            sendSuccessStatusBack();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                Utils.safeToast(getBaseContext(), "Weak password");
                                mDialog.dismiss();
                            }catch (FirebaseAuthUserCollisionException existEmail) {
                                // user exists, try sign in
                                signIn(email, password);
                            } catch (Exception e) {
                                Log.d(TAG, "onComplete: " + e.getMessage());
                                mDialog.dismiss();
                            }
                        }
                    }
                });
    }

    private void sendSuccessStatusBack() {
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
