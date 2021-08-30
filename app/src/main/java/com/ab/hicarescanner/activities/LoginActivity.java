package com.ab.hicarescanner.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.ab.hicarescanner.BaseActivity;
import com.ab.hicarescanner.R;
import com.ab.hicarescanner.databinding.ActivityLoginBinding;
import com.ab.hicarescanner.fragments.LoginFragment;
import com.ab.hicarescanner.utils.SharedPreferencesUtility;

public class LoginActivity extends BaseActivity {
ActivityLoginBinding mActivityLoginBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityLoginBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_login);
        if (SharedPreferencesUtility.getPrefBoolean(this, SharedPreferencesUtility.IS_USER_LOGIN)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            addFragment(LoginFragment.newInstance(), "LoginActivity-LoginFragment");
        }
    }
}
