package com.ab.hicarescanner.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ab.hicarescanner.BaseFragment;
import com.ab.hicarescanner.R;
import com.ab.hicarescanner.activities.HomeActivity;
import com.ab.hicarescanner.databinding.FragmentLoginBinding;
import com.ab.hicarescanner.handler.UserLoginClickHandler;
import com.ab.hicarescanner.network.NetworkCallController;
import com.ab.hicarescanner.network.NetworkResponseListner;
import com.ab.hicarescanner.network.model.login.LoginData;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements UserLoginClickHandler {
    FragmentLoginBinding mFragmentLoginBinding;
    private static final int LOGIN_REQUEST = 1000;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentLoginBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        mFragmentLoginBinding.layoutLogin.setHandler(this);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mFragmentLoginBinding.layoutLogin.checkbox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                // show password
                mFragmentLoginBinding.layoutLogin.editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // hide password
                mFragmentLoginBinding.layoutLogin.editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        return mFragmentLoginBinding.getRoot();
    }

    @Override
    public void onLoginClicked(View view) {
        try {
            if (getValidated()) {
                NetworkCallController controller = new NetworkCallController(this);
                String username = mFragmentLoginBinding.layoutLogin.editTextEmail.getText().toString();
                String password = mFragmentLoginBinding.layoutLogin.editTextPassword.getText().toString();
                controller.setListner(new NetworkResponseListner<LoginData>() {
                    @Override
                    public void onResponse(int requestCode, LoginData response) {
                        getRealm().beginTransaction();
                        getRealm().deleteAll();
                        getRealm().commitTransaction();
                        // add new record
                        getRealm().beginTransaction();
                        getRealm().copyToRealmOrUpdate(response);
                        getRealm().commitTransaction();
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    }

                    @Override
                    public void onFailure(int requestCode) {

                    }
                });
                controller.login(LOGIN_REQUEST, username, password);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private boolean getValidated() {
        if (mFragmentLoginBinding.layoutLogin.editTextEmail.getText().toString().length() == 0) {
            mFragmentLoginBinding.layoutLogin.editTextEmail.setError("Please enter username");
            return false;
        } else if (mFragmentLoginBinding.layoutLogin.editTextPassword.getText().toString().length() == 0) {
            mFragmentLoginBinding.layoutLogin.editTextPassword.setError("Please enter password");
            return false;
        } else {
            return true;
        }
    }
}
