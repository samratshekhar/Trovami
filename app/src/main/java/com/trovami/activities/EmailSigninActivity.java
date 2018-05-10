package com.trovami.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.trovami.R;
import com.trovami.databinding.ActivityEmailSigninBinding;

public class EmailSigninActivity extends AppCompatActivity {

    private ActivityEmailSigninBinding mBinding;

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
                validateCredentials();
            }
        });
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
            signIn(email, password);
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

    }
}
